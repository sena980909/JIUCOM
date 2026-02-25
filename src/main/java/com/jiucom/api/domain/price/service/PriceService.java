package com.jiucom.api.domain.price.service;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.price.dto.response.PriceComparisonResponse;
import com.jiucom.api.domain.price.dto.response.PriceHistoryResponse;
import com.jiucom.api.domain.price.entity.PriceEntry;
import com.jiucom.api.domain.price.entity.PriceHistory;
import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.domain.price.repository.PriceHistoryRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceService {

    private final PriceEntryRepository priceEntryRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PartRepository partRepository;
    private final RedisUtil redisUtil;

    private static final long PRICE_CACHE_TTL_MINUTES = 30;

    public PriceComparisonResponse getPriceComparison(Long partId) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));

        // Redis 캐시 시도 (실패 시 DB fallback)
        try {
            Object cached = redisUtil.getCachedPriceData(partId);
            if (cached instanceof PriceComparisonResponse response) {
                return response;
            }
        } catch (Exception e) {
            log.debug("Redis cache miss or unavailable for partId={}: {}", partId, e.getMessage());
        }

        List<PriceEntry> entries = priceEntryRepository.findByPartIdAndIsAvailableTrue(partId);

        List<PriceComparisonResponse.PriceEntryItem> priceItems = entries.stream()
                .map(PriceComparisonResponse.PriceEntryItem::from)
                .sorted(Comparator.comparing(PriceComparisonResponse.PriceEntryItem::getPrice))
                .toList();

        Integer lowest = priceItems.isEmpty() ? null : priceItems.get(0).getPrice();
        Integer highest = priceItems.isEmpty() ? null : priceItems.get(priceItems.size() - 1).getPrice();

        PriceComparisonResponse response = PriceComparisonResponse.builder()
                .partId(part.getId())
                .partName(part.getName())
                .lowestPrice(lowest)
                .highestPrice(highest)
                .prices(priceItems)
                .build();

        // Redis 캐시 저장 시도
        try {
            redisUtil.cachePriceData(partId, response, PRICE_CACHE_TTL_MINUTES);
        } catch (Exception e) {
            log.debug("Redis cache save failed for partId={}: {}", partId, e.getMessage());
        }

        return response;
    }

    public PriceHistoryResponse getPriceHistory(Long partId, String period) {
        Part part = partRepository.findById(partId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));

        int days = parsePeriodToDays(period);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<PriceHistory> histories = priceHistoryRepository
                .findByPartIdAndRecordDateBetweenOrderByRecordDateAsc(partId, startDate, endDate);

        // 일별 집계
        Map<LocalDate, List<PriceHistory>> grouped = histories.stream()
                .collect(Collectors.groupingBy(PriceHistory::getRecordDate));

        List<PriceHistoryResponse.DailyPricePoint> points = grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<PriceHistory> dayPrices = entry.getValue();
                    int min = dayPrices.stream().mapToInt(PriceHistory::getPrice).min().orElse(0);
                    int max = dayPrices.stream().mapToInt(PriceHistory::getPrice).max().orElse(0);
                    int avg = (int) dayPrices.stream().mapToInt(PriceHistory::getPrice).average().orElse(0);
                    return PriceHistoryResponse.DailyPricePoint.builder()
                            .date(entry.getKey())
                            .lowestPrice(min)
                            .highestPrice(max)
                            .averagePrice(avg)
                            .build();
                })
                .toList();

        return PriceHistoryResponse.builder()
                .partId(part.getId())
                .partName(part.getName())
                .period(period != null ? period : "30d")
                .history(points)
                .build();
    }

    private int parsePeriodToDays(String period) {
        if (period == null || period.isBlank()) {
            return 30;
        }
        try {
            return Integer.parseInt(period.replace("d", ""));
        } catch (NumberFormatException e) {
            return 30;
        }
    }
}
