package com.jiucom.api.domain.price.service;

import com.jiucom.api.domain.price.repository.PriceAlertRepository;
import com.jiucom.api.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceAlertService {

    private final PriceAlertRepository priceAlertRepository;
    private final NotificationService notificationService;

    // TODO: implement price alert CRUD and trigger logic
}
