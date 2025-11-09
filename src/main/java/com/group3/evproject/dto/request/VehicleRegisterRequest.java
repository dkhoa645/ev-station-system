package com.group3.evproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRegisterRequest {
    private Long modelId;
    private String licensePlate;
    private Long subscriptionPlanId; // bắt buộc chọn gói
}
