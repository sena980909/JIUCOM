package com.jiucom.api.global.oauth2;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getEmail();

    public abstract String getNickname();

    public abstract String getProfileImageUrl();
}
