package com.jiucom.api.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiucom.api.domain.user.dto.request.LoginRequest;
import com.jiucom.api.domain.user.dto.request.RefreshRequest;
import com.jiucom.api.domain.user.dto.request.SignupRequest;
import com.jiucom.api.domain.user.dto.response.JwtTokenResponse;
import com.jiucom.api.domain.user.service.AuthService;
import com.jiucom.api.global.config.SecurityConfig;
import com.jiucom.api.global.exception.ExceptionAdvice;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.jwt.JwtTokenProvider;
import com.jiucom.api.global.oauth2.CustomOAuth2UserService;
import com.jiucom.api.global.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.jiucom.api.global.oauth2.OAuth2FailureHandler;
import com.jiucom.api.global.oauth2.OAuth2SuccessHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, ExceptionAdvice.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockitoBean
    private OAuth2FailureHandler oAuth2FailureHandler;

    @MockitoBean
    private HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    private JwtTokenResponse mockTokenResponse() {
        return JwtTokenResponse.of("access-token", "refresh-token", 3600L);
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

    @Test
    @DisplayName("POST /auth/signup - 성공")
    void signup_success() throws Exception {
        given(authService.signup(any(SignupRequest.class))).willReturn(mockTokenResponse());

        SignupRequest request = createDto(SignupRequest.class,
                "email", "test@test.com", "password", "password123", "nickname", "testuser");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /auth/signup - 유효성 검증 실패 (이메일 형식)")
    void signup_invalidEmail() throws Exception {
        SignupRequest request = createDto(SignupRequest.class,
                "email", "not-an-email", "password", "password123", "nickname", "testuser");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login - 성공")
    void login_success() throws Exception {
        given(authService.login(any(LoginRequest.class))).willReturn(mockTokenResponse());

        LoginRequest request = createDto(LoginRequest.class,
                "email", "test@test.com", "password", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("POST /auth/login - 사용자 없음")
    void login_userNotFound() throws Exception {
        given(authService.login(any(LoginRequest.class)))
                .willThrow(new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        LoginRequest request = createDto(LoginRequest.class,
                "email", "notfound@test.com", "password", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("JIUCOM-U001"));
    }

    @Test
    @DisplayName("POST /auth/refresh - 성공")
    void refresh_success() throws Exception {
        given(authService.refresh(any(RefreshRequest.class))).willReturn(mockTokenResponse());

        RefreshRequest request = createDto(RefreshRequest.class,
                "refreshToken", "valid-refresh-token");

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    @DisplayName("POST /auth/logout - 성공")
    @WithMockUser(username = "1", roles = "USER")
    void logout_success() throws Exception {
        doNothing().when(authService).logout(any(Long.class));

        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
