package com.jiucom.api.global.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Long userId;
    private final String role;
    private final boolean newUser;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            Long userId,
                            String role,
                            boolean newUser) {
        super(authorities, attributes, nameAttributeKey);
        this.userId = userId;
        this.role = role;
        this.newUser = newUser;
    }
}
