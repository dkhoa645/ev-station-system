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
    @JoinColumn(name = "vehicle_id", nullable = false, unique = true)
    Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    SubscriptionPlan subscriptionPlan;

    @OneToOne(mappedBy = "vehicleSubscription", cascade = CascadeType.ALL)
    private VehicleSubscriptionUsage usage;

    LocalDateTime startDate;
    LocalDateTime endDate;
    @Enumerated(EnumType.STRING)
    private VehicleSubscriptionStatusEnum status; // active, expired, cancelled
    @Column(name = "auto_renew")
    boolean autoRenew;
}

