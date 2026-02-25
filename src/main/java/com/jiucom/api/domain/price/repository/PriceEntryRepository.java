package com.jiucom.api.domain.price.repository;

import com.jiucom.api.domain.price.entity.PriceEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceEntryRepository extends JpaRepository<PriceEntry, Long> {

    List<PriceEntry> findByPartIdAndIsAvailableTrue(Long partId);

    List<PriceEntry> findByPartIdOrderByPriceAsc(Long partId);
}
