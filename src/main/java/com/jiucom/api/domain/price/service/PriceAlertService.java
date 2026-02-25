package com.jiucom.api.domain.price.service;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.price.dto.request.PriceAlertCreateRequest;
import com.jiucom.api.domain.price.dto.response.PriceAlertResponse;
import com.jiucom.api.domain.price.entity.PriceAlert;
import com.jiucom.api.domain.price.repository.PriceAlertRepository;
import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.domain.notification.service.NotificationService;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceAlertService {

    private final PriceAlertRepository priceAlertRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;
    private final PriceEntryRepository priceEntryRepository;
    private final NotificationService notificationService;

    @Transactional
    public PriceAlertResponse createAlert(PriceAlertCreateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        Part part = partRepository.findById(request.getPartId())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));

        PriceAlert alert = PriceAlert.builder()
                .user(user)
                .part(part)
                .targetPrice(request.getTargetPrice())
                .build();

        priceAlertRepository.save(alert);

        return PriceAlertResponse.from(alert, part.getLowestPrice());
    }

    public List<PriceAlertResponse> getMyAlerts() {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        return priceAlertRepository.findByUserAndIsActiveTrue(user).stream()
                .map(alert -> PriceAlertResponse.from(alert, alert.getPart().getLowestPrice()))
                .toList();
    }

    @Transactional
    public void deactivateAlert(Long alertId) {
        Long userId = SecurityUtil.getCurrentUserId();
        PriceAlert alert = priceAlertRepository.findById(alertId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.RESOURCE_NOT_FOUND));

        if (!alert.getUser().getId().equals(userId)) {
            throw new GlobalException(GlobalErrorCode.FORBIDDEN);
        }

        alert.deactivate();
    }

    @Transactional
    public void checkAndTriggerAlerts(Long partId, Integer currentLowestPrice) {
        if (currentLowestPrice == null) return;

        List<PriceAlert> activeAlerts = priceAlertRepository
                .findByPartIdAndIsActiveTrueAndIsTriggeredFalse(partId);

        for (PriceAlert alert : activeAlerts) {
            if (currentLowestPrice <= alert.getTargetPrice()) {
                alert.trigger();
                log.info("Price alert triggered: alertId={}, partId={}, targetPrice={}, currentPrice={}",
                        alert.getId(), partId, alert.getTargetPrice(), currentLowestPrice);
                // TODO: NotificationService로 WebSocket 알림 전송 (Week 4)
            }
        }
    }
}
