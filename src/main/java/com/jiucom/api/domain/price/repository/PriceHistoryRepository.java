package com.jiucom.api.domain.price.repository;

import com.jiucom.api.domain.price.entity.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    List<PriceHistory> findByPartIdAndRecordDateBetweenOrderByRecordDateAsc(
            Long partId, LocalDate startDate, LocalDate endDate);
}
