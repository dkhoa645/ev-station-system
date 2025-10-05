package com.group3.evproject.controller;

import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.service.VehicleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/vehicle")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleController {

    VehicleService vehicleService;

    @GetMapping
    public ApiResponse<List<Vehicle>> getVehicles() {
        return ApiResponse.<List<Vehicle>>builder()
                .result(vehicleService.getVehicles())
                .build();
    }
}
