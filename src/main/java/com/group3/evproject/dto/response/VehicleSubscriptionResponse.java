package com.group3.evproject.dto.response;

import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleSubscriptionResponse {
    Long id;
    VehicleSubscriptionStatus status;
    String startDate;
    String endDate;
    SubscriptionPlanResponse subscriptionPlanResponse;
}
