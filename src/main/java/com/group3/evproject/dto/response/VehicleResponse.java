package com.group3.evproject.dto.response;

import com.group3.evproject.entity.SubscriptionPlan;
import com.group3.evproject.entity.VehicleModel;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleResponse {
    Long id;
    String licensePlate;
    VehicleModel model;
    SubscriptionPlanResponse subscriptionPlanResponse;
}
