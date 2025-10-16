package com.group3.evproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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
    BigDecimal price;
    @Column(name = "limit_value",precision = 10, scale = 2)
    BigDecimal limitValue;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name= "subscription_plan_description",
            joinColumns = @JoinColumn(name = "subscription_plan_id")
    )
    @Column(name = "description")
    List<String> description;
}
