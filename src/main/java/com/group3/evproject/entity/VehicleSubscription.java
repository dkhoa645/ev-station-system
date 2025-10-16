package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
    private Vehicle vehicle;

    @ManyToOne @JoinColumn(name = "subscription_id")
    SubscriptionPlan subscriptionPlan;

    java.time.LocalDateTime startDate;
    java.time.LocalDateTime endDate;
    String status; // active, expired, cancelled
}

