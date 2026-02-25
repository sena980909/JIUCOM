package com.jiucom.api.domain.price.crawler;

import com.jiucom.api.domain.seller.entity.Seller;

public interface CrawlerService {

    void crawl(Seller seller);

    boolean supports(String sellerName);
}
