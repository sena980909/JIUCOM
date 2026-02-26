package com.jiucom.api.domain.price.service;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.price.dto.response.PriceComparisonResponse;
import com.jiucom.api.domain.price.dto.response.PriceHistoryResponse;
import com.jiucom.api.domain.price.entity.PriceEntry;
import com.jiucom.api.domain.price.entity.PriceHistory;
import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.domain.price.repository.PriceHistoryRepository;
import com.jiucom.api.domain.seller.entity.Seller;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class PriceServiceTest {

    @InjectMocks
    private PriceService priceService;

    @Mock
    private PriceEntryRepository priceEntryRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private PartRepository partRepository;

    @Mock
    private RedisUtil redisUtil;

    private Part testPart;
    private Seller testSeller;

    @BeforeEach
    void setUp() {
        testPart = Part.builder()
                .name("AMD Ryzen 5 5600X")
                .category(PartCategory.CPU)
                .manufacturer("AMD")
                .lowestPrice(150000)
                .highestPrice(200000)
                .build();
        setId(testPart, 1L);

        testSeller = Seller.builder()
                .name("쿠팡")
                .siteUrl("https://coupang.com")
                .build();
        setId(testSeller, 1L);
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
    @DisplayName("가격 비교")
    class GetPriceComparison {

        @Test
        @DisplayName("성공 - DB에서 가격 비교 데이터 조회")
        void getPriceComparison_success() {
            given(partRepository.findById(1L)).willReturn(Optional.of(testPart));
            lenient().when(redisUtil.getCachedPriceData(1L)).thenReturn(null);

            PriceEntry entry = PriceEntry.builder()
                    .part(testPart)
                    .seller(testSeller)
                    .price(160000)
                    .productUrl("https://coupang.com/product/1")
                    .build();
            given(priceEntryRepository.findByPartIdAndIsAvailableTrue(1L)).willReturn(List.of(entry));

            PriceComparisonResponse response = priceService.getPriceComparison(1L);

            assertThat(response.getPartId()).isEqualTo(1L);
            assertThat(response.getPartName()).isEqualTo("AMD Ryzen 5 5600X");
            assertThat(response.getPrices()).hasSize(1);
            assertThat(response.getLowestPrice()).isEqualTo(160000);
        }

        @Test
        @DisplayName("실패 - 부품 없음")
        void getPriceComparison_partNotFound() {
            given(partRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> priceService.getPriceComparison(99L))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.PART_NOT_FOUND));
        }

        @Test
        @DisplayName("성공 - Redis 실패 시 DB fallback")
        void getPriceComparison_redisFallback() {
            given(partRepository.findById(1L)).willReturn(Optional.of(testPart));
            given(redisUtil.getCachedPriceData(1L)).willThrow(new RuntimeException("Redis down"));
            given(priceEntryRepository.findByPartIdAndIsAvailableTrue(1L)).willReturn(List.of());

            PriceComparisonResponse response = priceService.getPriceComparison(1L);

            assertThat(response.getPartId()).isEqualTo(1L);
            assertThat(response.getPrices()).isEmpty();
        }
    }

    @Nested
    @DisplayName("가격 이력")
    class GetPriceHistory {

        @Test
        @DisplayName("성공 - 30일 이력 조회")
        void getPriceHistory_success() {
            given(partRepository.findById(1L)).willReturn(Optional.of(testPart));

            PriceHistory history = PriceHistory.builder()
                    .part(testPart)
                    .seller(testSeller)
                    .price(155000)
                    .recordDate(LocalDate.now().minusDays(1))
                    .build();
            given(priceHistoryRepository.findByPartIdAndRecordDateBetweenOrderByRecordDateAsc(
                    eq(1L), any(LocalDate.class), any(LocalDate.class)))
                    .willReturn(List.of(history));

            PriceHistoryResponse response = priceService.getPriceHistory(1L, "30d");

            assertThat(response.getPartId()).isEqualTo(1L);
            assertThat(response.getHistory()).hasSize(1);
            assertThat(response.getHistory().get(0).getAveragePrice()).isEqualTo(155000);
        }

        @Test
        @DisplayName("실패 - 부품 없음")
        void getPriceHistory_partNotFound() {
            given(partRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> priceService.getPriceHistory(99L, "30d"))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.PART_NOT_FOUND));
        }

        @Test
        @DisplayName("성공 - null period는 기본값 30d")
        void getPriceHistory_nullPeriod() {
            given(partRepository.findById(1L)).willReturn(Optional.of(testPart));
            given(priceHistoryRepository.findByPartIdAndRecordDateBetweenOrderByRecordDateAsc(
                    eq(1L), any(LocalDate.class), any(LocalDate.class)))
                    .willReturn(List.of());

            PriceHistoryResponse response = priceService.getPriceHistory(1L, null);

            assertThat(response.getPeriod()).isEqualTo("30d");
        }
    }
}
