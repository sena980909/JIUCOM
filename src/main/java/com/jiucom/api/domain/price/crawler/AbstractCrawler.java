package com.jiucom.api.domain.price.crawler;

import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.domain.seller.entity.Seller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCrawler implements CrawlerService {

    protected final PriceEntryRepository priceEntryRepository;

    @Override
    public void crawl(Seller seller) {
        log.info("Starting crawl for seller: {}", seller.getName());
        try {
            doCrawl(seller);
        } catch (Exception e) {
            log.error("Crawl failed for seller: {}", seller.getName(), e);
        }
    }

    protected abstract void doCrawl(Seller seller);
}
