package com.group3.evproject.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionSummaryResponse {
    Long purchaseCount;
    BigDecimal revenue;
    Set<String> subscriber;
}
