package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "vehicle_subscription_usage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleSubscriptionUsage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne @JoinColumn(name = "vehicle_subscription_id")
    VehicleSubscription vehicleSubscription;

    Integer usedSessions;
    Double usedKwh;
    java.time.LocalDateTime resetDate;
}
