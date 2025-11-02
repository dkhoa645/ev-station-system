package com.group3.evproject.controller.admin;

import com.group3.evproject.dto.request.VehicleBrandRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.VehicleBrandResponse;
import com.group3.evproject.entity.VehicleBrand;
import com.group3.evproject.service.VehicleBrandService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vehicle-brand")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleBrandController {
    VehicleBrandService vehicleBrandService;

    @PostMapping
    public ApiResponse<VehicleBrand> create(@RequestBody VehicleBrandRequest vehicleBrandRequest) {
        return ApiResponse.<VehicleBrand>builder()
                .result(vehicleBrandService.createbrand(vehicleBrandRequest))
                .build();
    }
    @GetMapping("/{id}")
    public ApiResponse<VehicleBrandResponse> get(@PathVariable Long id) {
        return ApiResponse.<VehicleBrandResponse>builder()
                .result(vehicleBrandService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<VehicleBrandResponse> update(
            @PathVariable Long id,
            @RequestBody VehicleBrandRequest vehicleBrandRequest) {
        return ApiResponse.<VehicleBrandResponse>builder()
                .result(vehicleBrandService.update(id,vehicleBrandRequest))
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<VehicleBrandResponse>> getAllVehicleBranch() {
        return ApiResponse.<List<VehicleBrandResponse>>builder()
                .result(vehicleBrandService.findAll())
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable long id) {
        return ApiResponse.<String>builder()
                .result(vehicleBrandService.deleteById(id))
                .build();
    }



}
