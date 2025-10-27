package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicle_subscription")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "vehicle_id",  unique = true)
    Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = true)
    SubscriptionPlan subscriptionPlan;

    LocalDateTime startDate;
    LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    VehicleSubscriptionStatusEnum status; // active, expired, cancelled
    @Column(name = "auto_renew")
    boolean autoRenew;
    @OneToMany(mappedBy = "vehicleSubscription", cascade = CascadeType.ALL)
    List<PaymentTransaction> paymentTransactions = new ArrayList<>();
}

