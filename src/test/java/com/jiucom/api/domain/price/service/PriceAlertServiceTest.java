package com.jiucom.api.domain.price.service;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.price.entity.PriceAlert;
import com.jiucom.api.domain.price.repository.PriceAlertRepository;
import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.entity.enums.Role;
import com.jiucom.api.domain.user.entity.enums.SocialType;
import com.jiucom.api.domain.user.entity.enums.UserStatus;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.domain.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PriceAlertServiceTest {

    @InjectMocks
    private PriceAlertService priceAlertService;

    @Mock
    private PriceAlertRepository priceAlertRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PriceEntryRepository priceEntryRepository;

    @Mock
    private NotificationService notificationService;

    private User testUser;
    private Part testPart;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@test.com")
                .password("enc")
                .nickname("tester")
                .role(Role.USER)
                .socialType(SocialType.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();
        setId(testUser, 1L);

        testPart = Part.builder()
                .name("AMD Ryzen 5 5600X")
                .category(PartCategory.CPU)
                .manufacturer("AMD")
                .lowestPrice(150000)
                .build();
        setId(testPart, 1L);
    }

    private void setId(Object entity, Long id) {
        try {
            Field f = entity.getClass().getDeclaredField("id");
            f.setAccessible(true);
            f.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("알림 트리거")
    class CheckAndTriggerAlerts {

        @Test
        @DisplayName("성공 - 목표가 이하일 때 트리거")
        void checkAndTriggerAlerts_triggered() {
            PriceAlert alert = PriceAlert.builder()
                    .user(testUser)
                    .part(testPart)
                    .targetPrice(160000)
                    .build();
            setId(alert, 1L);

            given(priceAlertRepository.findByPartIdAndIsActiveTrueAndIsTriggeredFalse(1L))
                    .willReturn(List.of(alert));

            priceAlertService.checkAndTriggerAlerts(1L, 150000);

            assertThat(alert.isTriggered()).isTrue();
            assertThat(alert.isActive()).isFalse();
        }

        @Test
        @DisplayName("미트리거 - 현재 가격이 목표가보다 높음")
        void checkAndTriggerAlerts_notTriggered() {
            PriceAlert alert = PriceAlert.builder()
                    .user(testUser)
                    .part(testPart)
                    .targetPrice(100000)
                    .build();

            given(priceAlertRepository.findByPartIdAndIsActiveTrueAndIsTriggeredFalse(1L))
                    .willReturn(List.of(alert));

            priceAlertService.checkAndTriggerAlerts(1L, 150000);

            assertThat(alert.isTriggered()).isFalse();
            assertThat(alert.isActive()).isTrue();
        }

        @Test
        @DisplayName("null 가격이면 아무 동작 안 함")
        void checkAndTriggerAlerts_nullPrice() {
            priceAlertService.checkAndTriggerAlerts(1L, null);
            // no exception = success
        }
    }
}
