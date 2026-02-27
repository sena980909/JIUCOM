package com.jiucom.api.global.oauth2;

import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = OAuth2UserInfoFactory.getSocialType(registrationId);
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialType, oAuth2User.getAttributes());

        String socialId = userInfo.getId();
        Optional<User> existingUser = userRepository.findBySocialTypeAndSocialId(socialType, socialId);

        boolean isNewUser;
        User user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            isNewUser = false;
            log.info("기존 소셜 로그인 유저: {} ({})", user.getEmail(), socialType);
        } else {
            user = createUser(socialType, socialId, userInfo);
            isNewUser = true;
            log.info("신규 소셜 로그인 유저 생성: {} ({})", user.getEmail(), socialType);
        }

        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                oAuth2User.getAttributes(),
                nameAttributeKey,
                user.getId(),
                user.getRole().name(),
                isNewUser
        );
    }

    private User createUser(SocialType socialType, String socialId, OAuth2UserInfo userInfo) {
        String email = resolveEmail(socialType, socialId, userInfo.getEmail());
        String nickname = generateUniqueNickname(socialType, socialId);

        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .role(Role.USER)
                .socialType(socialType)
                .socialId(socialId)
                .status(UserStatus.ACTIVE)
                .profileImageUrl(userInfo.getProfileImageUrl())
                .build();

        return userRepository.save(user);
    }

    private String resolveEmail(SocialType socialType, String socialId, String email) {
        if (email == null || email.isBlank()) {
            return socialType.name().toLowerCase() + "_" + socialId + "@jiucom.local";
        }
        if (userRepository.existsByEmail(email)) {
            return socialType.name().toLowerCase() + "_" + socialId + "@jiucom.local";
        }
        return email;
    }

    private String generateUniqueNickname(SocialType socialType, String socialId) {
        String shortId = socialId.length() > 8 ? socialId.substring(0, 8) : socialId;
        String baseNickname = socialType.name().toLowerCase() + "_" + shortId;

        if (!userRepository.existsByNickname(baseNickname)) {
            return baseNickname;
        }

        for (int i = 1; i <= 100; i++) {
            String candidate = baseNickname + i;
            if (!userRepository.existsByNickname(candidate)) {
                return candidate;
            }
        }

        return baseNickname + System.currentTimeMillis();
    }
}
