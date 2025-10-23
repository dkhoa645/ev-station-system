package com.group3.evproject.dto.response;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanResponse {
    Long id;
    String name;
    BigDecimal price;
    BigDecimal limitValue;
    List<String> description;
}
