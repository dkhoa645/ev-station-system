package com.group3.evproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {
    private Long modelId;
    private String licensePlate;
    // thêm thông tin gói đăng ký
    private Long subscriptionPlanId; // bắt buộc chọn gói
}
