package com.jiucom.api.domain.seller.service;

import com.jiucom.api.domain.seller.dto.response.SellerResponse;
import com.jiucom.api.domain.seller.entity.enums.SellerStatus;
import com.jiucom.api.domain.seller.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SellerService {

    private final SellerRepository sellerRepository;

    public List<SellerResponse> getActiveSellers() {
        return sellerRepository.findByStatus(SellerStatus.ACTIVE).stream()
                .map(SellerResponse::from)
                .toList();
    }
}
