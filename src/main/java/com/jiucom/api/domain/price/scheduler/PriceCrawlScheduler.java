package com.jiucom.api.domain.price.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceCrawlScheduler {

    // TODO: inject CrawlerService when implemented

    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours
    public void crawlPrices() {
        log.info("Starting price crawl job...");
        // TODO: implement price crawling orchestration
        log.info("Price crawl job completed.");
    }
}
