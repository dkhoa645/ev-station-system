package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_subscription_usage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleSubscriptionUsage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_subscription_id", unique = true)
    private VehicleSubscription vehicleSubscription;
    @Column(name = "limit_kwh",precision = 10, scale = 2)
    BigDecimal limitKwh;
    @Column(name = "used_kwh",precision = 10, scale = 2)
    BigDecimal usedKwh ;
    LocalDateTime resetDate;
}
