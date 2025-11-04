package com.group3.evproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionPlanRequest {
    @NotBlank(message = "Name is required")
    String name;
    @NotNull(message = "Price is required")
    BigDecimal price;
    @NotNull(message = "Discount is required")
    BigDecimal discount;
    List<String> description;
}
