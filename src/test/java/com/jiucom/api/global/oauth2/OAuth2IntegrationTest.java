package com.jiucom.api.global.oauth2;

import com.jiucom.api.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OAuth2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("GET /auth/oauth2/urls - 소셜 로그인 URL 목록 반환 (구글, 네이버)")
    void getOAuth2Urls() throws Exception {
        mockMvc.perform(get("/auth/oauth2/urls"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.google").exists())
                .andExpect(jsonPath("$.data.naver").exists())
                .andExpect(jsonPath("$.data.kakao").doesNotExist());
    }

    @Test
    @DisplayName("GET /oauth2/authorize/google - Google OAuth2 리다이렉트")
    void googleOAuth2Redirect() throws Exception {
        MvcResult result = mockMvc.perform(get("/oauth2/authorize/google"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertThat(redirectUrl).contains("accounts.google.com");
    }

    @Test
    @DisplayName("GET /oauth2/authorize/naver - Naver OAuth2 리다이렉트")
    void naverOAuth2Redirect() throws Exception {
        MvcResult result = mockMvc.perform(get("/oauth2/authorize/naver"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String redirectUrl = result.getResponse().getRedirectedUrl();
        assertThat(redirectUrl).contains("nid.naver.com");
    }
}
