package com.jiucom.api.domain.seller.repository;

import com.jiucom.api.domain.seller.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    Optional<Seller> findByName(String name);
}
