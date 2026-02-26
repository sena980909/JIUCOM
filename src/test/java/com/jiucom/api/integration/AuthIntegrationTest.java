package com.jiucom.api.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static String accessToken;
    private static String refreshToken;

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = jsonHeaders();
        headers.setBearerAuth(accessToken);
        return headers;
    }

    @Test
    @Order(1)
    @DisplayName("회원가입 → 토큰 반환")
    void signup() {
        String body = """
                {"email":"integration@test.com","password":"password123","nickname":"통합테스터"}
                """;

        ResponseEntity<Map> response = restTemplate.exchange(
                "/auth/signup", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("accessToken")).isNotNull();
        accessToken = (String) data.get("accessToken");
        refreshToken = (String) data.get("refreshToken");
    }

    @Test
    @Order(2)
    @DisplayName("로그인 → 토큰 반환")
    void login() {
        String body = """
                {"email":"integration@test.com","password":"password123"}
                """;

        ResponseEntity<Map> response = restTemplate.exchange(
                "/auth/login", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("accessToken")).isNotNull();
        accessToken = (String) data.get("accessToken");
        refreshToken = (String) data.get("refreshToken");
    }

    @Test
    @Order(3)
    @DisplayName("프로필 조회 (인증)")
    void getProfile() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/users/me", HttpMethod.GET,
                new HttpEntity<>(authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("email")).isEqualTo("integration@test.com");
        assertThat(data.get("nickname")).isEqualTo("통합테스터");
    }

    @Test
    @Order(4)
    @DisplayName("로그아웃")
    void logout() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/auth/logout", HttpMethod.POST,
                new HttpEntity<>(authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(5)
    @DisplayName("중복 이메일 회원가입 실패")
    void signup_duplicateEmail() {
        String body1 = """
                {"email":"dup@test.com","password":"password123","nickname":"닉네임1"}
                """;
        restTemplate.exchange("/auth/signup", HttpMethod.POST,
                new HttpEntity<>(body1, jsonHeaders()), Map.class);

        String body2 = """
                {"email":"dup@test.com","password":"password123","nickname":"닉네임2"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                "/auth/signup", HttpMethod.POST,
                new HttpEntity<>(body2, jsonHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
