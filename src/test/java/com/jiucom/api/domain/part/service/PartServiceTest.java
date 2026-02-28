package com.jiucom.api.domain.part.service;

import com.jiucom.api.domain.part.dto.request.PartCreateRequest;
import com.jiucom.api.domain.part.dto.request.PartSearchRequest;
import com.jiucom.api.domain.part.dto.request.PartUpdateRequest;
import com.jiucom.api.domain.part.dto.response.PartDetailResponse;
import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.price.entity.PriceEntry;
import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.response.PageResponse;
import com.jiucom.api.global.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartServiceTest {

    @InjectMocks
    private PartService partService;

    @Mock
    private PartRepository partRepository;

    @Mock
    private PriceEntryRepository priceEntryRepository;

    @Mock
    private RedisUtil redisUtil;

    private Part testPart;

    @BeforeEach
    void setUp() {
        testPart = Part.builder()
                .name("AMD Ryzen 5 5600X")
                .category(PartCategory.CPU)
                .manufacturer("AMD")
                .modelNumber("100-100000065BOX")
                .specs("{\"cores\":6,\"threads\":12}")
                .lowestPrice(150000)
                .highestPrice(200000)
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
    @DisplayName("부품 검색")
    class SearchParts {

        @Test
        @DisplayName("성공 - 키워드로 검색")
        void searchParts_success() {
            Page<Part> page = new PageImpl<>(List.of(testPart));
            given(partRepository.searchParts(any(), any(), any(), any(), any(Pageable.class)))
                    .willReturn(page);

            PartSearchRequest request = new PartSearchRequest();
            setFieldValue(request, "keyword", "Ryzen");
            setFieldValue(request, "page", 0);
            setFieldValue(request, "size", 20);

            PageResponse result = partService.searchParts(request);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("부품 상세 조회")
    class GetPartDetail {

        @Test
        @DisplayName("성공 - 부품 상세")
        void getPartDetail_success() {
            given(partRepository.findById(1L)).willReturn(Optional.of(testPart));
            given(priceEntryRepository.findByPartIdOrderByPriceAsc(1L)).willReturn(List.of());

            PartDetailResponse response = partService.getPartDetail(1L);

            assertThat(response.getName()).isEqualTo("AMD Ryzen 5 5600X");
            assertThat(response.getCategory()).isEqualTo("CPU");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 부품")
        void getPartDetail_notFound() {
            given(partRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> partService.getPartDetail(99L))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.PART_NOT_FOUND));
        }
    }

    @Nested
    @DisplayName("카테고리 목록")
    class GetCategories {

        @Test
        @DisplayName("성공 - 전체 카테고리 반환")
        void getAllCategories_success() {
            given(redisUtil.getCachedPartCategories()).willReturn(null);

            List<String> categories = partService.getAllCategories();

            assertThat(categories).contains("CPU", "GPU", "RAM", "MOTHERBOARD");
            assertThat(categories).hasSizeGreaterThan(5);
        }
    }

    @Nested
    @DisplayName("부품 생성 (Admin)")
    class CreatePart {

        @Test
        @DisplayName("성공 - 부품 생성")
        void createPart_success() {
            given(partRepository.save(any(Part.class))).willAnswer(invocation -> {
                Part p = invocation.getArgument(0);
                setId(p, 2L);
                return p;
            });

            PartCreateRequest request = new PartCreateRequest();
            setFieldValue(request, "name", "Intel i7-13700K");
            setFieldValue(request, "category", PartCategory.CPU);
            setFieldValue(request, "manufacturer", "Intel");
            setFieldValue(request, "lowestPrice", 400000);
            setFieldValue(request, "highestPrice", 500000);

            PartDetailResponse response = partService.createPart(request);

            assertThat(response.getName()).isEqualTo("Intel i7-13700K");
            verify(partRepository).save(any(Part.class));
        }
    }

    @Nested
    @DisplayName("부품 삭제 (Admin)")
    class DeletePart {

        @Test
        @DisplayName("성공 - 소프트 삭제")
        void deletePart_success() {
            given(partRepository.findById(1L)).willReturn(Optional.of(testPart));

            partService.deletePart(1L);

            assertThat(testPart.isDeleted()).isTrue();
        }

        @Test
        @DisplayName("실패 - 부품 없음")
        void deletePart_notFound() {
            given(partRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> partService.deletePart(99L))
                    .isInstanceOf(GlobalException.class)
                    .satisfies(ex -> assertThat(((GlobalException) ex).getErrorCode())
                            .isEqualTo(GlobalErrorCode.PART_NOT_FOUND));
        }
    }

    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field f = obj.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
