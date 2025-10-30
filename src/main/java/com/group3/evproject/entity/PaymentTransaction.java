package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // ✅ khớp với các entity khác
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // 🔹 Liên kết tới gói subscription
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_subscription_id")
    VehicleSubscription vehicleSubscription;

    // 🔹 Liên kết tới booking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    Booking booking;

    // 🔹 Liên kết tới payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    Payment payment;

    // 🔹 Người thực hiện giao dịch
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // 🔹 Thông tin giao dịch
    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal amount;

    String paymentMethod;

    @Column(unique = true, nullable = false)
    String vnpTxnRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    com.group3.evproject.Enum.PaymentTransaction status;

    LocalDateTime createdAt;
    LocalDateTime expiresAt;
    LocalDateTime paidAt;

    String bankCode = "VNBANK";
}
