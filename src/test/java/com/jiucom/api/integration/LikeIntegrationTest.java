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
class LikeIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static String accessToken;
    private static Long postId;

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
    @DisplayName("사전 준비 - 회원가입 + 게시글 생성")
    void setup() {
        String signupBody = """
                {"email":"like@test.com","password":"password123","nickname":"좋아요테스터"}
                """;
        ResponseEntity<Map> signupRes = restTemplate.exchange(
                "/auth/signup", HttpMethod.POST,
                new HttpEntity<>(signupBody, jsonHeaders()), Map.class);
        Map signupData = (Map) signupRes.getBody().get("data");
        accessToken = (String) signupData.get("accessToken");

        String postBody = """
                {"boardType":"FREE","title":"좋아요 테스트","content":"좋아요 테스트 내용"}
                """;
        ResponseEntity<Map> postRes = restTemplate.exchange(
                "/posts", HttpMethod.POST,
                new HttpEntity<>(postBody, authHeaders()), Map.class);
        Map postData = (Map) postRes.getBody().get("data");
        postId = ((Number) postData.get("id")).longValue();
    }

    @Test
    @Order(2)
    @DisplayName("좋아요 추가 (토글)")
    void toggleLike_add() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/likes/POST/" + postId, HttpMethod.POST,
                new HttpEntity<>(authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("liked")).isEqualTo(true);
        assertThat(((Number) data.get("likeCount")).longValue()).isEqualTo(1L);
    }

    @Test
    @Order(3)
    @DisplayName("좋아요 상태 조회")
    void getLikeStatus() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/likes/POST/" + postId, HttpMethod.GET,
                new HttpEntity<>(authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("liked")).isEqualTo(true);
    }

    @Test
    @Order(4)
    @DisplayName("좋아요 취소 (토글)")
    void toggleLike_remove() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/likes/POST/" + postId, HttpMethod.POST,
                new HttpEntity<>(authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("liked")).isEqualTo(false);
        assertThat(((Number) data.get("likeCount")).longValue()).isEqualTo(0L);
    }
}
