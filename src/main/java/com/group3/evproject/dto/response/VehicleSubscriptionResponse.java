package com.group3.evproject.dto.response;

import com.group3.evproject.entity.VehicleSubscriptionStatusEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleSubscriptionResponse {
    Long id;
    VehicleSubscriptionStatusEnum status;
    String startDate;
    String endDate;
    SubscriptionPlanResponse subscriptionPlanResponse;
}
