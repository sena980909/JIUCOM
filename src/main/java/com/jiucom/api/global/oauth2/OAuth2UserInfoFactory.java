package com.jiucom.api.global.oauth2;

import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;

import java.util.Map;

public class OAuth2UserInfoFactory {

    private OAuth2UserInfoFactory() {
    }

    public static OAuth2UserInfo getOAuth2UserInfo(SocialType socialType, Map<String, Object> attributes) {
        return switch (socialType) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            default -> throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_PROVIDER);
        };
    }

    public static SocialType getSocialType(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> SocialType.GOOGLE;
            case "naver" -> SocialType.NAVER;
            default -> throw new GlobalException(GlobalErrorCode.OAUTH_INVALID_PROVIDER);
        };
    }
}
