package com.jiucom.api.global.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;

class MockEmailServiceTest {

    private final MockEmailService mockEmailService = new MockEmailService();

    @Test
    @DisplayName("MockEmailService - 이메일 발송 (로그만 출력, 예외 없음)")
    void sendEmail_logsOnly() {
        assertThatCode(() -> mockEmailService.sendEmail(
                "test@test.com",
                "테스트 제목",
                "email-verification",
                Map.of("nickname", "테스터", "code", "123456")
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("MockEmailService - 가격 알림 이메일")
    void sendEmail_priceAlert() {
        assertThatCode(() -> mockEmailService.sendEmail(
                "user@test.com",
                "가격 알림",
                "price-alert",
                Map.of("partName", "RTX 4070", "currentPrice", "500000", "targetPrice", "450000")
        )).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("MockEmailService - 댓글 알림 이메일")
    void sendEmail_commentNotification() {
        assertThatCode(() -> mockEmailService.sendEmail(
                "author@test.com",
                "새 댓글 알림",
                "comment-notification",
                Map.of("postTitle", "게시글", "commenterNickname", "댓글러", "commentContent", "좋은 글입니다")
        )).doesNotThrowAnyException();
    }
}
