package com.jiucom.api.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiucom.api.domain.user.dto.request.UpdateProfileRequest;
import com.jiucom.api.domain.user.dto.response.UserProfileResponse;
import com.jiucom.api.domain.user.service.UserService;
import com.jiucom.api.global.config.SecurityConfig;
import com.jiucom.api.global.exception.ExceptionAdvice;
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
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, ExceptionAdvice.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

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

    private UserProfileResponse mockProfileResponse() {
        return UserProfileResponse.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("testuser")
                .profileImageUrl(null)
                .role("USER")
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();
    }

    @Test
    @DisplayName("GET /users/me - 성공")
    @WithMockUser(username = "1", roles = "USER")
    void getMyProfile_success() throws Exception {
        given(userService.getProfile(any(Long.class))).willReturn(mockProfileResponse());

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("testuser"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    @DisplayName("GET /users/me - 인증 없음")
    void getMyProfile_unauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("PATCH /users/me - 닉네임 변경 성공")
    @WithMockUser(username = "1", roles = "USER")
    void updateProfile_success() throws Exception {
        UserProfileResponse updatedResponse = UserProfileResponse.builder()
                .id(1L)
                .email("test@test.com")
                .nickname("newnick")
                .role("USER")
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();

        given(userService.updateProfile(any(Long.class), any(UpdateProfileRequest.class)))
                .willReturn(updatedResponse);

        UpdateProfileRequest request = createDto(UpdateProfileRequest.class,
                "nickname", "newnick");

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("newnick"));
    }

    @Test
    @DisplayName("PATCH /users/me - 닉네임 유효성 실패 (1자)")
    @WithMockUser(username = "1", roles = "USER")
    void updateProfile_invalidNickname() throws Exception {
        UpdateProfileRequest request = createDto(UpdateProfileRequest.class,
                "nickname", "x");

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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
}
