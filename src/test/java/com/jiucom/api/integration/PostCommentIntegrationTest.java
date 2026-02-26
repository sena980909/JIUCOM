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
class PostCommentIntegrationTest {

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
    @DisplayName("사전 준비 - 회원가입")
    void setup() {
        String body = """
                {"email":"postcmt@test.com","password":"password123","nickname":"게시글테스터"}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                "/auth/signup", HttpMethod.POST,
                new HttpEntity<>(body, jsonHeaders()), Map.class);
        Map data = (Map) response.getBody().get("data");
        accessToken = (String) data.get("accessToken");
    }

    @Test
    @Order(2)
    @DisplayName("게시글 생성")
    void createPost() {
        String body = """
                {"boardType":"FREE","title":"통합 테스트 게시글","content":"테스트 내용입니다."}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                "/posts", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map data = (Map) response.getBody().get("data");
        postId = ((Number) data.get("id")).longValue();
        assertThat(data.get("title")).isEqualTo("통합 테스트 게시글");
    }

    @Test
    @Order(3)
    @DisplayName("게시글 상세 조회 (viewCount 증가)")
    void getPostDetail() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/posts/" + postId, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("viewCount")).isEqualTo(1);
    }

    @Test
    @Order(4)
    @DisplayName("댓글 생성")
    void createComment() {
        String body = """
                {"content":"통합 테스트 댓글입니다."}
                """;
        ResponseEntity<Map> response = restTemplate.exchange(
                "/posts/" + postId + "/comments", HttpMethod.POST,
                new HttpEntity<>(body, authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Map data = (Map) response.getBody().get("data");
        assertThat(data.get("content")).isEqualTo("통합 테스트 댓글입니다.");
    }

    @Test
    @Order(5)
    @DisplayName("게시글 목록 조회")
    void getPosts() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/posts?page=0&size=20", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(6)
    @DisplayName("게시글 삭제")
    void deletePost() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/posts/" + postId, HttpMethod.DELETE,
                new HttpEntity<>(authHeaders()), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
