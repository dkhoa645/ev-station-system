package com.group3.evproject.controller.company;

import com.group3.evproject.dto.request.CompanyUserCreationRequest;
import com.group3.evproject.dto.request.CompanyVehicleCreationRequest;
import com.group3.evproject.dto.request.VehicleRegisterRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.service.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/company/vehicle")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyVehicleController {

    VehicleService vehicleService;

    @GetMapping("/all")
    public ApiResponse<List<VehicleResponse>> getVehicles() {
        return ApiResponse.<List<VehicleResponse>>builder()
                .result(vehicleService.getAllCompanyVehicles())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ApiResponse.<VehicleResponse>builder()
                .result(vehicleService.getById(id))
                .build();
    }

    @PostMapping()
    public ApiResponse<VehicleResponse> createVehicle(
            @RequestBody CompanyVehicleCreationRequest companyVehicleCreationRequest
    ) {
        return ApiResponse.<VehicleResponse>builder()
                .result(vehicleService.createCompanyVehicle(companyVehicleCreationRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteVehicleById(
            @PathVariable Long id)
    {
        String message = vehicleService.deleteByUserAndId(id);
        return ApiResponse.<String>builder()
                        .result(message)
                        .build();
    }

}
