package com.jiucom.api.domain.seller.repository;

import com.jiucom.api.domain.seller.entity.Seller;
import com.jiucom.api.domain.seller.entity.enums.SellerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByName(String name);

    List<Seller> findByStatus(SellerStatus status);
}
