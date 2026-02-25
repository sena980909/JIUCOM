package com.jiucom.api.domain.payment.entity;

import com.jiucom.api.domain.build.entity.Build;
import com.jiucom.api.domain.payment.entity.enums.PaymentMethod;
import com.jiucom.api.domain.payment.entity.enums.PaymentStatus;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "build_id")
    private Build build;

    @Column(nullable = false)
    private Integer totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    private String pgTransactionId; // PG사 거래 ID (향후 실결제 연동용)

    private String failReason;

    public void complete(String pgTransactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.pgTransactionId = pgTransactionId;
    }

    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failReason = reason;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }
}
