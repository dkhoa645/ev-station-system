package com.group3.evproject.entity;

import com.group3.evproject.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    BigDecimal totalEnergy;
    BigDecimal totalCost;
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
    LocalDateTime paidAt;
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<Invoice> invoices ;
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactions;

    @OneToOne(fetch = FetchType.LAZY)   // 1 Payment chỉ gắn với 1 Subscription
    @JoinColumn(name = "vehicle_subscription_id", nullable = false)
    VehicleSubscription vehicleSubscription;

}
