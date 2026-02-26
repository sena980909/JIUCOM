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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisUtil redisUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .password("encodedPassword")
                .nickname("testuser")
                .role(Role.USER)
                .socialType(SocialType.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();
        setId(testUser, 1L);
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SignupRequest createSignupRequest() {
        return createDto(SignupRequest.class, "email", "test@test.com",
                "password", "password123", "nickname", "testuser");
    }

    private LoginRequest createLoginRequest() {
        return createDto(LoginRequest.class, "email", "test@test.com",
                "password", "password123");
    }

    private RefreshRequest createRefreshRequest(String token) {
        return createDto(RefreshRequest.class, "refreshToken", token);
    }

    @SuppressWarnings("unchecked")
    private <T> T createDto(Class<T> clazz, String... fieldValues) {
        try {
            T dto = clazz.getDeclaredConstructor().newInstance();
            for (int i = 0; i < fieldValues.length; i += 2) {
                Field field = clazz.getDeclaredField(fieldValues[i]);
                field.setAccessible(true);
                field.set(dto, fieldValues[i + 1]);
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void mockGenerateTokens() {
        lenient().when(jwtTokenProvider.createAccessToken(any(), any())).thenReturn("access-token");
        lenient().when(jwtTokenProvider.createRefreshToken(any(), any())).thenReturn("refresh-token");
        lenient().when(jwtTokenProvider.getRefreshTokenValidity()).thenReturn(604800000L);
        lenient().when(jwtTokenProvider.getAccessTokenValidity()).thenReturn(3600000L);
        lenient().when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(RefreshToken.builder()
                .user(testUser).token("refresh-token").expiresAt(LocalDateTime.now().plusDays(7)).build());
    }

    private void mockUserSaveWithId() {
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            setId(user, 1L);
            return user;
        });
    }

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("성공 - 정상 회원가입 시 토큰 반환")
        void signup_success() {
            given(userRepository.existsByEmail("test@test.com")).willReturn(false);
            given(userRepository.existsByNickname("testuser")).willReturn(false);
            given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
            mockUserSaveWithId();
            mockGenerateTokens();

            JwtTokenResponse response = authService.signup(createSignupRequest());

            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("실패 - 중복 이메일")
        void signup_duplicateEmail() {
            given(userRepository.existsByEmail("test@test.com")).willReturn(true);

            assertThatThrownBy(() -> authService.signup(createSignupRequest()))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.DUPLICATE_EMAIL));
        }

        @Test
        @DisplayName("실패 - 중복 닉네임")
        void signup_duplicateNickname() {
            given(userRepository.existsByEmail("test@test.com")).willReturn(false);
            given(userRepository.existsByNickname("testuser")).willReturn(true);

            assertThatThrownBy(() -> authService.signup(createSignupRequest()))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.DUPLICATE_NICKNAME));
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공 - 정상 로그인")
        void login_success() {
            given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
            mockGenerateTokens();

            JwtTokenResponse response = authService.login(createLoginRequest());

            assertThat(response.getAccessToken()).isEqualTo("access-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void login_userNotFound() {
            given(userRepository.findByEmail("test@test.com")).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(createLoginRequest()))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.USER_NOT_FOUND));
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void login_invalidPassword() {
            given(userRepository.findByEmail("test@test.com")).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(false);

            assertThatThrownBy(() -> authService.login(createLoginRequest()))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.INVALID_PASSWORD));
        }
    }

    @Nested
    @DisplayName("토큰 갱신")
    class Refresh {

        @Test
        @DisplayName("성공 - 토큰 로테이션")
        void refresh_success() {
            String oldToken = "old-refresh-token";
            RefreshToken dbToken = RefreshToken.builder()
                    .user(testUser)
                    .token(oldToken)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .build();

            given(jwtTokenProvider.validateToken(oldToken)).willReturn(true);
            given(jwtTokenProvider.getUserId(oldToken)).willReturn(1L);
            given(redisUtil.getRefreshToken(1L)).willReturn(oldToken);
            given(refreshTokenRepository.findByToken(oldToken)).willReturn(Optional.of(dbToken));
            mockGenerateTokens();

            JwtTokenResponse response = authService.refresh(createRefreshRequest(oldToken));

            assertThat(response.getAccessToken()).isEqualTo("access-token");
            verify(refreshTokenRepository).delete(dbToken);
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 토큰")
        void refresh_invalidToken() {
            given(jwtTokenProvider.validateToken("invalid-token")).willReturn(false);

            assertThatThrownBy(() -> authService.refresh(createRefreshRequest("invalid-token")))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.INVALID_TOKEN));
        }

        @Test
        @DisplayName("성공 - Redis 미연결 시 DB fallback")
        void refresh_redisFallback() {
            String token = "refresh-token-value";
            RefreshToken dbToken = RefreshToken.builder()
                    .user(testUser)
                    .token(token)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .build();

            given(jwtTokenProvider.validateToken(token)).willReturn(true);
            given(jwtTokenProvider.getUserId(token)).willReturn(1L);
            given(redisUtil.getRefreshToken(1L)).willThrow(new RuntimeException("Redis connection failed"));
            given(refreshTokenRepository.findByToken(token)).willReturn(Optional.of(dbToken));
            mockGenerateTokens();

            JwtTokenResponse response = authService.refresh(createRefreshRequest(token));

            assertThat(response.getAccessToken()).isEqualTo("access-token");
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("성공 - 로그아웃 시 토큰 삭제")
        void logout_success() {
            given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

            authService.logout(1L);

            verify(refreshTokenRepository).deleteByUser(testUser);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void logout_userNotFound() {
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> authService.logout(1L))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.USER_NOT_FOUND));
        }
    }
}
