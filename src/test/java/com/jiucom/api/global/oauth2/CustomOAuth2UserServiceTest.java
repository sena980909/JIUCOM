package com.jiucom.api.global.oauth2;

import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomOAuth2UserServiceTest {

    @Test
    @DisplayName("OAuth2UserInfoFactory - Google 사용자 정보 파싱")
    void googleUserInfoParsing() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google-123");
        attributes.put("email", "user@gmail.com");
        attributes.put("name", "Test User");
        attributes.put("picture", "https://example.com/photo.jpg");

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(SocialType.GOOGLE, attributes);

        assertThat(userInfo.getId()).isEqualTo("google-123");
        assertThat(userInfo.getEmail()).isEqualTo("user@gmail.com");
        assertThat(userInfo.getNickname()).isEqualTo("Test User");
        assertThat(userInfo.getProfileImageUrl()).isEqualTo("https://example.com/photo.jpg");
    }

    @Test
    @DisplayName("OAuth2UserInfoFactory - Naver 사용자 정보 파싱")
    void naverUserInfoParsing() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", "naver-id-123");
        response.put("email", "user@naver.com");
        response.put("name", "네이버유저");
        response.put("profile_image", "https://phinf.naver.net/photo.jpg");

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("response", response);

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(SocialType.NAVER, attributes);

        assertThat(userInfo.getId()).isEqualTo("naver-id-123");
        assertThat(userInfo.getEmail()).isEqualTo("user@naver.com");
        assertThat(userInfo.getNickname()).isEqualTo("네이버유저");
        assertThat(userInfo.getProfileImageUrl()).isEqualTo("https://phinf.naver.net/photo.jpg");
    }

    @Test
    @DisplayName("OAuth2UserInfoFactory - Naver response 없는 경우 null 반환")
    void naverUserInfoWithoutResponse() {
        Map<String, Object> attributes = new HashMap<>();

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(SocialType.NAVER, attributes);

        assertThat(userInfo.getId()).isNull();
        assertThat(userInfo.getEmail()).isNull();
    }

    @Test
    @DisplayName("OAuth2UserInfoFactory - registrationId로 SocialType 변환")
    void getSocialTypeFromRegistrationId() {
        assertThat(OAuth2UserInfoFactory.getSocialType("google")).isEqualTo(SocialType.GOOGLE);
        assertThat(OAuth2UserInfoFactory.getSocialType("naver")).isEqualTo(SocialType.NAVER);
        assertThat(OAuth2UserInfoFactory.getSocialType("GOOGLE")).isEqualTo(SocialType.GOOGLE);
    }

    @Test
    @DisplayName("OAuth2UserInfoFactory - 잘못된 provider는 예외 발생")
    void invalidProviderThrowsException() {
        assertThatThrownBy(() -> OAuth2UserInfoFactory.getSocialType("invalid"))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    @DisplayName("OAuth2UserInfoFactory - LOCAL 타입은 예외 발생")
    void localTypeThrowsException() {
        assertThatThrownBy(() ->
                OAuth2UserInfoFactory.getOAuth2UserInfo(SocialType.LOCAL, Map.of()))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    @DisplayName("CookieUtils - 직렬화 동작")
    void cookieSerialize() {
        String testValue = "test-state-value";
        String serialized = CookieUtils.serialize(testValue);
        assertThat(serialized).isNotNull();
        assertThat(serialized).isNotEmpty();
    }

    @Test
    @DisplayName("CustomOAuth2User - 필드 검증")
    void customOAuth2UserFields() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123");

        CustomOAuth2User user = new CustomOAuth2User(
                Collections.emptyList(),
                attributes,
                "sub",
                1L,
                "USER",
                true
        );

        assertThat(user.getUserId()).isEqualTo(1L);
        assertThat(user.getRole()).isEqualTo("USER");
        assertThat(user.isNewUser()).isTrue();
    }

    @Test
    @DisplayName("CustomOAuth2User - 기존 유저 (newUser=false)")
    void customOAuth2UserExistingUser() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "456");

        CustomOAuth2User user = new CustomOAuth2User(
                Collections.emptyList(),
                attributes,
                "sub",
                2L,
                "ADMIN",
                false
        );

        assertThat(user.getUserId()).isEqualTo(2L);
        assertThat(user.getRole()).isEqualTo("ADMIN");
        assertThat(user.isNewUser()).isFalse();
    }
}
