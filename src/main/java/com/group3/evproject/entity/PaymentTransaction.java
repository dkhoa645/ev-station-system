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
    @JoinColumn(name = "vehicle_subscription_id", nullable = true)
    VehicleSubscription vehicleSubscription;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = true)
    Booking booking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = true)
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
    @Column(nullable = false, length = 20)
    PaymentTransactionEnum status;
    LocalDateTime createdAt;
    LocalDateTime paidAt;
    String bankCode = "VNBANK";

}
