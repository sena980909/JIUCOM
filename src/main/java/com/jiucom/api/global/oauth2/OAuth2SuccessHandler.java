package com.jiucom.api.global.oauth2;

import com.jiucom.api.domain.user.dto.response.JwtTokenResponse;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.domain.user.service.AuthService;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Value("${oauth2.redirect-uri:http://localhost:3000/oauth/callback}")
    private String defaultRedirectUri;

    public OAuth2SuccessHandler(@Lazy AuthService authService,
                                UserRepository userRepository,
                                HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        User user = userRepository.findById(oAuth2User.getUserId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        JwtTokenResponse tokenResponse = authService.generateTokens(user);

        String targetUrl = determineTargetUrl(request, tokenResponse, oAuth2User.isNewUser());

        clearAuthenticationAttributes(request, response);

        log.info("OAuth2 로그인 성공: userId={}, newUser={}", oAuth2User.getUserId(), oAuth2User.isNewUser());

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String determineTargetUrl(HttpServletRequest request,
                                      JwtTokenResponse tokenResponse,
                                      boolean isNewUser) {
        Optional<String> redirectUri = CookieUtils.getCookie(request,
                        HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUri = redirectUri.orElse(defaultRedirectUri);

        return UriComponentsBuilder.fromUriString(targetUri)
                .queryParam("accessToken", tokenResponse.getAccessToken())
                .queryParam("refreshToken", tokenResponse.getRefreshToken())
                .queryParam("isNewUser", isNewUser)
                .build().toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
