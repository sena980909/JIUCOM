package com.jiucom.api.global.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class NaverShoppingClient {

    private static final String SHOPPING_API_URL = "https://openapi.naver.com/v1/search/shop.json";

    private final RestTemplate restTemplate;
    private final NaverShoppingConfig config;

    public NaverShoppingClient(
            @Qualifier("naverShoppingRestTemplate") RestTemplate restTemplate,
            NaverShoppingConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public NaverShoppingResponse search(String query, int display, int start, String sort) {
        if (!config.isConfigured()) {
            log.warn("Naver Shopping API is not configured. Set NAVER_SHOPPING_CLIENT_ID and NAVER_SHOPPING_CLIENT_SECRET.");
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", config.getClientId());
        headers.set("X-Naver-Client-Secret", config.getClientSecret());

        URI uri = UriComponentsBuilder.fromHttpUrl(SHOPPING_API_URL)
                .queryParam("query", query)
                .queryParam("display", display)
                .queryParam("start", start)
                .queryParam("sort", sort)
                .encode()
                .build()
                .toUri();

        try {
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<NaverShoppingResponse> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, NaverShoppingResponse.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Naver Shopping API call failed for query '{}': {}", query, e.getMessage());
            return null;
        }
    }

    // --- Response DTOs ---

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverShoppingResponse {
        private String lastBuildDate;
        private int total;
        private int start;
        private int display;
        private List<NaverShoppingItem> items;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NaverShoppingItem {
        private String title;
        private String link;
        private String image;
        private String lprice;   // lowest price (String)
        private String hprice;   // highest price (String)
        private String mallName;
        private String productId;
        private String productType;
        private String maker;
        private String brand;
        private String category1;
        private String category2;
        private String category3;
        private String category4;

        public String getCleanTitle() {
            return title == null ? "" : title.replaceAll("<[^>]*>", "").trim();
        }

        public Integer getLpriceInt() {
            try {
                return lprice != null && !lprice.isBlank() ? Integer.parseInt(lprice) : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public Integer getHpriceInt() {
            try {
                return hprice != null && !hprice.isBlank() ? Integer.parseInt(hprice) : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public String getEffectiveMaker() {
            if (maker != null && !maker.isBlank()) return maker;
            if (brand != null && !brand.isBlank()) return brand;
            return "Unknown";
        }
    }
}
