package com.jiucom.api.domain.price.service;

import com.jiucom.api.domain.price.repository.PriceEntryRepository;
import com.jiucom.api.domain.price.repository.PriceHistoryRepository;
import com.jiucom.api.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceService {

    private final PriceEntryRepository priceEntryRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final RedisUtil redisUtil;

    // TODO: implement price comparison and history operations
}
