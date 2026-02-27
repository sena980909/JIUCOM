package com.jiucom.api.global.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Value("${oauth2.redirect-uri:http://localhost:3000/oauth/callback}")
    private String defaultRedirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        Optional<String> redirectUri = CookieUtils.getCookie(request,
                        HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUri = redirectUri.orElse(defaultRedirectUri);

        String targetUrl = UriComponentsBuilder.fromUriString(targetUri)
                .queryParam("error", URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8))
                .build().toUriString();

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        log.error("OAuth2 로그인 실패: {}", exception.getMessage());

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
