package com.group3.evproject.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_subscription_id")
    VehicleSubscription vehicleSubscription;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    Booking booking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    Payment payment;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal amount;
    String paymentMethod;
    @Column(unique = true, nullable = false)
    String vnpTxnRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentTransactionEnum status;

    LocalDateTime createdAt = LocalDateTime.now();
    LocalDateTime paidAt;

    String orderInfo;

    String bankCode = "VNBANK";

}
