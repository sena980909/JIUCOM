package com.jiucom.api.domain.price.repository;

import com.jiucom.api.domain.price.entity.PriceAlert;
import com.jiucom.api.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

    List<PriceAlert> findByUserAndIsActiveTrue(User user);

    List<PriceAlert> findByPartIdAndIsActiveTrueAndIsTriggeredFalse(Long partId);
}
