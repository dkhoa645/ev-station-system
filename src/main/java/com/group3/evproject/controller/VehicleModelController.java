package com.group3.evproject.controller;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.service.VehicleModelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/model")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleModelController {
    VehicleModelService vehicleModelService;

    @GetMapping()
    public ApiResponse<List<VehicleModel>> getVehicleModel() {
        return ApiResponse.<List<VehicleModel>>builder()
                .result(vehicleModelService.getAllModel())
                .build();
    }
    @PostMapping("/bulk")
    public ApiResponse<List<VehicleModel>> addVehicleModels(@RequestBody List<VehicleModelRequest> models) {
        List<VehicleModel> savedModels = vehicleModelService.saveAllModel(models);
        return ApiResponse.<List<VehicleModel>>builder()
                .result(savedModels)
                .build();
    }
}
