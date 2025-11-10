package com.group3.evproject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vehicle_subscription")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id", nullable = false, unique = true)
    @JsonBackReference
    Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    @JsonManagedReference
    SubscriptionPlan subscriptionPlan;

    LocalDateTime startDate;
    LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    VehicleSubscriptionStatus status; // active, expired, cancelled

    @Column(name = "auto_renew")
    boolean autoRenew;

    @OneToMany(mappedBy = "vehicleSubscription", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<PaymentTransaction> paymentTransactions ;

}

