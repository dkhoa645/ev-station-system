package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "vehicle_subscription")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne @JoinColumn(name = "vehicle_id")
    Vehicle vehicle;

    @ManyToOne @JoinColumn(name = "subscription_id")
    SubscriptionPlan subscriptionPlan;

    java.time.LocalDateTime startDate;
    java.time.LocalDateTime endDate;
    String status; // active, expired, cancelled
}

