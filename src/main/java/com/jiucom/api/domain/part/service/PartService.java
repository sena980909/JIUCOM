package com.jiucom.api.domain.part.service;

import com.jiucom.api.domain.part.dto.request.PartCreateRequest;
import com.jiucom.api.domain.part.dto.request.PartSearchRequest;
import com.jiucom.api.domain.part.dto.request.PartUpdateRequest;
import com.jiucom.api.domain.part.dto.response.PartDetailResponse;
import com.jiucom.api.domain.part.dto.response.PartListResponse;
import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.price.entity.PriceEntry;
import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.response.PageResponse;
import com.jiucom.api.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartService {

    private final PartRepository partRepository;
    private final PriceEntryRepository priceEntryRepository;
    private final RedisUtil redisUtil;

    public PageResponse<PartListResponse> searchParts(PartSearchRequest request) {
        Sort sort = parseSort(request.getSort());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Part> page = partRepository.searchParts(
                request.getKeyword(),
                request.getCategory(),
                request.getMinPrice(),
                request.getMaxPrice(),
                pageable
        );

        return PageResponse.from(page, PartListResponse::from);
    }

    public PartDetailResponse getPartDetail(Long partId) {
        // Check cache
        PartDetailResponse cached = redisUtil.getCachedPartDetail(partId);
        if (cached != null) {
            return cached;
        }

        Part part = partRepository.findById(partId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));

        List<PriceEntry> priceEntries = priceEntryRepository.findByPartIdOrderByPriceAsc(partId);
        PartDetailResponse response = PartDetailResponse.of(part, priceEntries);

        // Store in cache
        redisUtil.cachePartDetail(partId, response);

        return response;
    }

    public List<String> getAllCategories() {
        // Check cache
        List<String> cached = redisUtil.getCachedPartCategories();
        if (cached != null) {
            return cached;
        }

        List<String> categories = Arrays.stream(PartCategory.values())
                .map(PartCategory::name)
                .toList();

        // Store in cache (24h TTL)
        redisUtil.cachePartCategories(categories);

        return categories;
    }

    @Transactional
    public PartDetailResponse createPart(PartCreateRequest request) {
        Part part = Part.builder()
                .name(request.getName())
                .category(request.getCategory())
                .manufacturer(request.getManufacturer())
                .modelNumber(request.getModelNumber())
                .imageUrl(request.getImageUrl())
                .specs(request.getSpecs())
                .lowestPrice(request.getLowestPrice())
                .highestPrice(request.getHighestPrice())
                .build();

        partRepository.save(part);
        return PartDetailResponse.of(part, List.of());
    }

    @Transactional
    public PartDetailResponse updatePart(Long partId, PartUpdateRequest request) {
        Part part = partRepository.findById(partId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));

        if (request.getName() != null) {
            part.updateName(request.getName());
        }
        if (request.getCategory() != null) {
            part.updateCategory(request.getCategory());
        }
        if (request.getManufacturer() != null) {
            part.updateManufacturer(request.getManufacturer());
        }
        if (request.getSpecs() != null) {
            part.updateSpecs(request.getSpecs());
        }
        if (request.getLowestPrice() != null || request.getHighestPrice() != null) {
            part.updatePriceRange(
                    request.getLowestPrice() != null ? request.getLowestPrice() : part.getLowestPrice(),
                    request.getHighestPrice() != null ? request.getHighestPrice() : part.getHighestPrice()
            );
        }

        List<PriceEntry> priceEntries = priceEntryRepository.findByPartIdOrderByPriceAsc(partId);
        PartDetailResponse response = PartDetailResponse.of(part, priceEntries);

        // Invalidate cache
        redisUtil.evictPartDetail(partId);

        return response;
    }

    @Transactional
    public void deletePart(Long partId) {
        Part part = partRepository.findById(partId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));
        part.softDelete();

        // Invalidate cache
        redisUtil.evictPartDetail(partId);
    }

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.unsorted();
        }
        return switch (sortParam) {
            case "popular" -> Sort.by(Sort.Direction.DESC, "popularity");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.unsorted();
        };
    }
}
