package com.jiucom.api.domain.payment.repository;

import com.jiucom.api.domain.payment.entity.Payment;
import com.jiucom.api.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderNumber(String orderNumber);

    Page<Payment> findByUserAndIsDeletedFalse(User user, Pageable pageable);
}
