package com.jiucom.api.global.config.interceptor;

import com.jiucom.api.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateLimitInterceptorTest {

    private RateLimitInterceptor interceptor;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        interceptor = new RateLimitInterceptor(5);
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("제한 이내 요청은 허용")
    void allowRequestsWithinLimit() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 5; i++) {
            assertThat(interceptor.preHandle(request, response, null)).isTrue();
        }
    }

    @Test
    @DisplayName("제한 초과 시 예외 발생")
    void rejectRequestsOverLimit() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        for (int i = 0; i < 5; i++) {
            interceptor.preHandle(request, response, null);
        }

        assertThatThrownBy(() -> interceptor.preHandle(request, response, null))
                .isInstanceOf(GlobalException.class);
    }

    @Test
    @DisplayName("다른 IP는 별도 카운트")
    void separateCountPerIp() throws Exception {
        MockHttpServletRequest req1 = new MockHttpServletRequest();
        req1.setRemoteAddr("10.0.0.1");
        MockHttpServletRequest req2 = new MockHttpServletRequest();
        req2.setRemoteAddr("10.0.0.2");

        for (int i = 0; i < 5; i++) {
            interceptor.preHandle(req1, response, null);
        }

        assertThat(interceptor.preHandle(req2, response, null)).isTrue();
    }

    @Test
    @DisplayName("X-Forwarded-For 헤더에서 IP 추출")
    void extractIpFromXForwardedFor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");

        assertThat(interceptor.preHandle(request, response, null)).isTrue();
    }
}
