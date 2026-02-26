package com.jiucom.api.domain.notification.service;

import com.jiucom.api.domain.notification.dto.response.NotificationCountResponse;
import com.jiucom.api.domain.notification.entity.Notification;
import com.jiucom.api.domain.notification.entity.enums.NotificationType;
import com.jiucom.api.domain.notification.repository.NotificationRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.TestSecurityContextHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com").password("enc").nickname("tester")
                .role(Role.USER).socialType(SocialType.LOCAL).status(UserStatus.ACTIVE).build();
        setId(testUser, 1L);

        TestSecurityContextHelper.setAuthentication(1L);
    }

    @AfterEach
    void tearDown() {
        TestSecurityContextHelper.clearAuthentication();
    }

    private void setId(Object entity, Long id) {
        try {
            Field f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @DisplayName("읽지 않은 알림 수 조회")
    void getUnreadCount_success() {
        given(notificationRepository.countByUserIdAndIsReadFalseAndIsDeletedFalse(1L)).willReturn(5L);

        NotificationCountResponse response = notificationService.getUnreadCount();

        assertThat(response.getUnreadCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("알림 읽음 처리 - 본인 알림만 가능")
    void markAsRead_notOwner() {
        Notification notification = Notification.builder()
                .user(testUser).type(NotificationType.COMMENT_REPLY)
                .title("알림").message("메시지").build();
        setId(notification, 1L);

        TestSecurityContextHelper.setAuthentication(999L);
        given(notificationRepository.findById(1L)).willReturn(Optional.of(notification));

        assertThatThrownBy(() -> notificationService.markAsRead(1L))
                .isInstanceOf(GlobalException.class)
                .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                        .isEqualTo(GlobalErrorCode.FORBIDDEN));
    }

    @Test
    @DisplayName("알림 전체 읽음 처리")
    void markAllAsRead_success() {
        notificationService.markAllAsRead();

        verify(notificationRepository).markAllAsReadByUserId(1L);
    }

    @Test
    @DisplayName("알림 전송 - DB 저장 + WebSocket")
    void sendNotification_success() {
        given(userRepository.findById(1L)).willReturn(Optional.of(testUser));
        given(notificationRepository.save(any(Notification.class))).willAnswer(i -> i.getArgument(0));

        notificationService.sendNotification(1L, NotificationType.COMMENT_REPLY,
                "알림 제목", "알림 내용", "/posts/1");

        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(eq("/queue/notifications/1"), (Object) any());
    }
}
