package com.jiucom.api.domain.price.scheduler;

import com.jiucom.api.domain.price.crawler.CrawlerService;
import com.jiucom.api.domain.price.service.PriceAlertService;
import com.jiucom.api.domain.seller.entity.Seller;
import com.jiucom.api.domain.seller.entity.enums.SellerStatus;
import com.jiucom.api.domain.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCrawlScheduler {

    private final SellerRepository sellerRepository;
    private final PriceAlertService priceAlertService;
    private final List<CrawlerService> crawlerServices;

    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours
    public void crawlPrices() {
        log.info("Starting price crawl job...");

        List<Seller> activeSellers = sellerRepository.findByStatus(SellerStatus.ACTIVE);

        for (Seller seller : activeSellers) {
            crawlerServices.stream()
                    .filter(crawler -> crawler.supports(seller.getName()))
                    .findFirst()
                    .ifPresent(crawler -> {
                        try {
                            crawler.crawl(seller);
                            log.info("Crawled prices for seller: {}", seller.getName());
                        } catch (Exception e) {
                            log.error("Failed to crawl prices for seller: {}", seller.getName(), e);
                        }
                    });
        }

        log.info("Price crawl job completed.");
    }
}
