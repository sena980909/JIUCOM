package com.jiucom.api.global.naver;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.price.entity.PriceEntry;
import com.jiucom.api.domain.price.entity.PriceHistory;
import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.domain.price.repository.PriceHistoryRepository;
import com.jiucom.api.domain.seller.entity.Seller;
import com.jiucom.api.domain.seller.entity.enums.SellerStatus;
import com.jiucom.api.domain.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverShoppingService {

    private final NaverShoppingClient client;
    private final NaverShoppingConfig config;
    private final PartRepository partRepository;
    private final SellerRepository sellerRepository;
    private final PriceEntryRepository priceEntryRepository;
    private final PriceHistoryRepository priceHistoryRepository;

    // Category → search keywords mapping (general Korean shopping terms)
    private static final Map<PartCategory, List<String>> CATEGORY_KEYWORDS = Map.ofEntries(
            Map.entry(PartCategory.CPU, List.of(
                    "데스크탑 CPU", "인텔 CPU", "AMD 라이젠 CPU", "게이밍 CPU"
            )),
            Map.entry(PartCategory.GPU, List.of(
                    "데스크탑 그래픽카드", "지포스 그래픽카드", "라데온 그래픽카드", "게이밍 그래픽카드"
            )),
            Map.entry(PartCategory.RAM, List.of(
                    "데스크탑 DDR5 메모리", "데스크탑 DDR4 메모리", "삼성 데스크탑 램", "게이밍 메모리 램"
            )),
            Map.entry(PartCategory.SSD, List.of(
                    "NVMe SSD", "M.2 SSD 1TB", "삼성 SSD", "데스크탑 SSD"
            )),
            Map.entry(PartCategory.MOTHERBOARD, List.of(
                    "데스크탑 메인보드", "인텔 메인보드", "AMD 메인보드", "게이밍 메인보드"
            )),
            Map.entry(PartCategory.POWER_SUPPLY, List.of(
                    "컴퓨터 파워서플라이", "데스크탑 파워", "80플러스 파워서플라이", "게이밍 파워"
            )),
            Map.entry(PartCategory.CASE, List.of(
                    "컴퓨터 케이스", "데스크탑 케이스", "미들타워 케이스", "게이밍 PC 케이스"
            )),
            Map.entry(PartCategory.COOLER, List.of(
                    "CPU 쿨러", "타워쿨러", "수냉쿨러", "공랭쿨러"
            ))
    );

    @Transactional
    public NaverImportResult importAll() {
        if (!config.isConfigured()) {
            return NaverImportResult.error("네이버 쇼핑 API가 설정되지 않았습니다. NAVER_SHOPPING_CLIENT_ID, NAVER_SHOPPING_CLIENT_SECRET 환경변수를 설정하세요.");
        }

        int totalParts = 0;
        int totalPriceEntries = 0;
        int totalSellers = 0;
        Map<String, Integer> categoryResults = new LinkedHashMap<>();

        for (Map.Entry<PartCategory, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            PartCategory category = entry.getKey();
            List<String> keywords = entry.getValue();
            int categoryCount = 0;

            for (String keyword : keywords) {
                try {
                    int[] result = importKeyword(keyword, category);
                    categoryCount += result[0];
                    totalPriceEntries += result[1];
                    totalSellers += result[2];
                    // Rate limit: max 10 calls/sec → sleep 150ms between calls
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Failed to import keyword '{}': {}", keyword, e.getMessage());
                }
            }

            totalParts += categoryCount;
            categoryResults.put(category.name(), categoryCount);
            log.info("Category {} imported: {} parts", category, categoryCount);
        }

        return NaverImportResult.success(totalParts, totalPriceEntries, totalSellers, categoryResults);
    }

    @Transactional
    public NaverImportResult importCategory(PartCategory category) {
        if (!config.isConfigured()) {
            return NaverImportResult.error("네이버 쇼핑 API가 설정되지 않았습니다.");
        }

        List<String> keywords = CATEGORY_KEYWORDS.get(category);
        if (keywords == null) {
            return NaverImportResult.error("지원하지 않는 카테고리: " + category);
        }

        int totalParts = 0;
        int totalPriceEntries = 0;
        int totalSellers = 0;

        for (String keyword : keywords) {
            try {
                int[] result = importKeyword(keyword, category);
                totalParts += result[0];
                totalPriceEntries += result[1];
                totalSellers += result[2];
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Failed to import keyword '{}': {}", keyword, e.getMessage());
            }
        }

        return NaverImportResult.success(totalParts, totalPriceEntries, totalSellers,
                Map.of(category.name(), totalParts));
    }

    /**
     * Import products from a single keyword search.
     * @return [partsCreated, priceEntriesCreated, sellersCreated]
     */
    private int[] importKeyword(String keyword, PartCategory category) {
        NaverShoppingClient.NaverShoppingResponse response = client.search(keyword, 20, 1, "sim");
        if (response == null) {
            log.warn("Naver API returned null for keyword '{}'", keyword);
            return new int[]{0, 0, 0};
        }
        if (response.getItems() == null || response.getItems().isEmpty()) {
            log.warn("Naver API returned empty items for keyword '{}' (total: {})", keyword, response.getTotal());
            return new int[]{0, 0, 0};
        }
        log.info("Naver API returned {} items for keyword '{}' (total: {})", response.getItems().size(), keyword, response.getTotal());

        int partsCreated = 0;
        int priceEntriesCreated = 0;
        int sellersCreated = 0;
        LocalDate today = LocalDate.now();

        for (NaverShoppingClient.NaverShoppingItem item : response.getItems()) {
            String cleanTitle = item.getCleanTitle();
            Integer lowPrice = item.getLpriceInt();

            if (cleanTitle.isBlank() || lowPrice == null || lowPrice <= 0) {
                continue;
            }

            // 1. Find or create Seller
            String mallName = item.getMallName();
            if (mallName == null || mallName.isBlank()) {
                mallName = "네이버쇼핑";
            }

            String sellerName = mallName;
            Seller seller = sellerRepository.findByName(sellerName).orElse(null);
            if (seller == null) {
                seller = sellerRepository.save(Seller.builder()
                        .name(sellerName)
                        .siteUrl(item.getLink() != null ? extractDomain(item.getLink()) : "https://shopping.naver.com")
                        .status(SellerStatus.ACTIVE)
                        .reliabilityScore(3.0)
                        .build());
                sellersCreated++;
            }

            // 2. Find or create Part (by name similarity check)
            Part part = findExistingPart(cleanTitle, category);
            if (part == null) {
                part = partRepository.save(Part.builder()
                        .name(truncate(cleanTitle, 200))
                        .category(category)
                        .manufacturer(truncate(item.getEffectiveMaker(), 100))
                        .imageUrl(item.getImage())
                        .lowestPrice(lowPrice)
                        .highestPrice(item.getHpriceInt() != null ? item.getHpriceInt() : lowPrice)
                        .build());
                partsCreated++;
            } else {
                // Update price range if better
                updatePriceRange(part, lowPrice, item.getHpriceInt());
                if (part.getImageUrl() == null && item.getImage() != null) {
                    // Part doesn't have setter for imageUrl via update method, so we rebuild
                }
            }

            // 3. Create PriceEntry
            PriceEntry priceEntry = PriceEntry.builder()
                    .part(part)
                    .seller(seller)
                    .price(lowPrice)
                    .productUrl(item.getLink())
                    .isAvailable(true)
                    .build();
            priceEntryRepository.save(priceEntry);
            priceEntriesCreated++;

            // 4. Create PriceHistory snapshot
            PriceHistory history = PriceHistory.builder()
                    .part(part)
                    .seller(seller)
                    .price(lowPrice)
                    .recordDate(today)
                    .build();
            priceHistoryRepository.save(history);
        }

        log.info("Keyword '{}': {} parts, {} prices, {} sellers", keyword, partsCreated, priceEntriesCreated, sellersCreated);
        return new int[]{partsCreated, priceEntriesCreated, sellersCreated};
    }

    private Part findExistingPart(String title, PartCategory category) {
        // Exact name match only to avoid false positives
        String truncatedTitle = truncate(title, 200);
        return partRepository.findByNameContainingIgnoreCase(truncatedTitle,
                org.springframework.data.domain.PageRequest.of(0, 1))
                .getContent()
                .stream()
                .filter(p -> p.getCategory() == category)
                .filter(p -> p.getName().equalsIgnoreCase(truncatedTitle))
                .findFirst()
                .orElse(null);
    }

    private void updatePriceRange(Part part, Integer newLow, Integer newHigh) {
        Integer currentLow = part.getLowestPrice();
        Integer currentHigh = part.getHighestPrice();

        Integer updatedLow = currentLow == null ? newLow : Math.min(currentLow, newLow);
        Integer updatedHigh = currentHigh == null ? (newHigh != null ? newHigh : newLow) :
                (newHigh != null ? Math.max(currentHigh, newHigh) : currentHigh);

        part.updatePriceRange(updatedLow, updatedHigh);
    }

    private String extractDomain(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (Exception e) {
            return url;
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return null;
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }

    // --- Result DTO ---

    public record NaverImportResult(
            boolean success,
            String message,
            int totalParts,
            int totalPriceEntries,
            int totalSellers,
            Map<String, Integer> categoryResults
    ) {
        public static NaverImportResult success(int parts, int prices, int sellers, Map<String, Integer> categories) {
            return new NaverImportResult(true,
                    String.format("네이버 쇼핑 데이터 임포트 완료: %d개 부품, %d개 가격정보, %d개 판매자", parts, prices, sellers),
                    parts, prices, sellers, categories);
        }

        public static NaverImportResult error(String message) {
            return new NaverImportResult(false, message, 0, 0, 0, Map.of());
        }
    }
}
