package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "subscription_plan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String type;         // prepaid, postpaid
    String name;
    Double price;
    String billingCycle; // monthly, yearly
    String limitType;    // session_count, kwh, unlimited
    Integer limitValue;
    String description;
}
