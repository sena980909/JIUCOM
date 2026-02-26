package com.jiucom.api.domain.user.service;

import com.jiucom.api.domain.user.dto.request.LoginRequest;
import com.jiucom.api.domain.user.dto.request.RefreshRequest;
import com.jiucom.api.domain.user.dto.request.SignupRequest;
import com.jiucom.api.domain.user.dto.response.JwtTokenResponse;
import com.jiucom.api.domain.user.entity.RefreshToken;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.RefreshTokenRepository;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.jwt.JwtTokenProvider;
import com.jiucom.api.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    @Transactional
    public JwtTokenResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new GlobalException(GlobalErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new GlobalException(GlobalErrorCode.DUPLICATE_NICKNAME);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .socialType(SocialType.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);
        return generateTokens(user);
    }

    @Transactional
    public JwtTokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new GlobalException(GlobalErrorCode.INVALID_PASSWORD);
        }

        return generateTokens(user);
    }

    @Transactional
    public JwtTokenResponse refresh(RefreshRequest request) {
        String token = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(token)) {
            throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(token);

        // Redis 검증 (Redis 미연결 시 DB fallback)
        try {
            String storedToken = redisUtil.getRefreshToken(userId);
            if (storedToken != null && !storedToken.equals(token)) {
                throw new GlobalException(GlobalErrorCode.INVALID_TOKEN);
            }
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Redis unavailable for refresh token check, falling back to DB: {}", e.getMessage());
        }

        // DB 검증
        RefreshToken dbToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.INVALID_TOKEN));

        User user = dbToken.getUser();

        // 기존 토큰 삭제 (로테이션)
        refreshTokenRepository.delete(dbToken);
        tryDeleteRedisRefreshToken(userId);

        return generateTokens(user);
    }

    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        refreshTokenRepository.deleteByUser(user);
        tryDeleteRedisRefreshToken(userId);
    }

    private JwtTokenResponse generateTokens(User user) {
        String role = user.getRole().name();
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), role);
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), role);

        long refreshValidity = jwtTokenProvider.getRefreshTokenValidity();

        // 기존 리프레시 토큰 삭제 (로그인 시 기존 세션 무효화)
        refreshTokenRepository.deleteByUser(user);
        tryDeleteRedisRefreshToken(user.getId());

        // DB 저장
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(refreshValidity / 1000))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        // Redis 저장 (Redis 미연결 시 무시)
        try {
            redisUtil.saveRefreshToken(user.getId(), refreshToken, refreshValidity);
        } catch (Exception e) {
            log.warn("Redis unavailable, refresh token saved to DB only: {}", e.getMessage());
        }

        return JwtTokenResponse.of(
                accessToken,
                refreshToken,
                jwtTokenProvider.getAccessTokenValidity() / 1000
        );
    }

    private void tryDeleteRedisRefreshToken(Long userId) {
        try {
            redisUtil.deleteRefreshToken(userId);
        } catch (Exception e) {
            log.warn("Redis unavailable for refresh token deletion: {}", e.getMessage());
        }
    }
}
