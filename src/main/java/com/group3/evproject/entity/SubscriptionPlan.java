package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_plan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;

    @Column(precision = 10, scale = 2)
    Double price;

    @Column(name = "discount_percent",precision = 10, scale = 2)
    Double discount ;

    @Column(name = "multiplier",precision = 10, scale = 2)
    Double multiplier;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name= "subscription_plan_description",
            joinColumns = @JoinColumn(name = "subscription_plan_id")
    )
    @Column(name = "description")
    List<String> description;
    @OneToMany(mappedBy = "subscriptionPlan")
    List<VehicleSubscription> vehicleSubscriptions = new ArrayList<>();
}
