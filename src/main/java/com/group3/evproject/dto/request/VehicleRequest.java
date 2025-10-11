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

    String userId;

    @NotBlank(message = "License plate is required")
    String licensePlate;

    @NotBlank(message = "Model is required")
    String model;


    Integer companyId;
}
