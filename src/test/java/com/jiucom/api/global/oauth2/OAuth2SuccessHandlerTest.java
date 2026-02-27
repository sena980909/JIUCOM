package com.jiucom.api.global.oauth2;

import com.jiucom.api.domain.user.dto.response.JwtTokenResponse;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.domain.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @InjectMocks
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    private User testUser;
    private JwtTokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(oAuth2SuccessHandler, "defaultRedirectUri", "http://localhost:3000/oauth/callback");

        testUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("google_abc12345")
                .role(Role.USER)
                .socialType(SocialType.GOOGLE)
                .socialId("google-123")
                .status(UserStatus.ACTIVE)
                .build();

        tokenResponse = JwtTokenResponse.of("test-access-token", "test-refresh-token", 3600L);
    }

    @Test
    @DisplayName("소셜 로그인 성공 시 JWT 토큰과 함께 리다이렉트")
    void successRedirectWithTokens() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google-123");

        CustomOAuth2User oAuth2User = new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub",
                1L,
                "USER",
                false
        );

        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(oAuth2User);
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(authService.generateTokens(any(User.class))).willReturn(tokenResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        String redirectUrl = response.getRedirectedUrl();
        assertThat(redirectUrl).isNotNull();
        assertThat(redirectUrl).contains("accessToken=test-access-token");
        assertThat(redirectUrl).contains("refreshToken=test-refresh-token");
        assertThat(redirectUrl).contains("isNewUser=false");
        assertThat(redirectUrl).startsWith("http://localhost:3000/oauth/callback");
    }

    @Test
    @DisplayName("신규 유저 소셜 로그인 시 isNewUser=true")
    void newUserRedirectWithIsNewUserTrue() throws Exception {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "google-456");

        CustomOAuth2User oAuth2User = new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub",
                2L,
                "USER",
                true
        );

        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(oAuth2User);
        given(userRepository.findById(2L)).willReturn(Optional.of(testUser));
        given(authService.generateTokens(any(User.class))).willReturn(tokenResponse);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);

        String redirectUrl = response.getRedirectedUrl();
        assertThat(redirectUrl).contains("isNewUser=true");
    }
}
