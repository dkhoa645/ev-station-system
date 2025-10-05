package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne @JoinColumn(name = "session_id")
    ChargingSession session;

    @ManyToOne @JoinColumn(name = "user_id")
    User user;

    @ManyToOne @JoinColumn(name = "company_id")
    Company company;

    @ManyToOne @JoinColumn(name = "vehicle_subscription_id")
    VehicleSubscription vehicleSubscription;

    Double amount;
    String status; // pending, paid, failed
}
