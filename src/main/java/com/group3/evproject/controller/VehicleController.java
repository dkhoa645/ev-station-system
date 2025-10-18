package com.group3.evproject.controller;

import com.group3.evproject.dto.request.VehicleRegisterRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.service.VehicleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

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
                .result(vehicleService.getAllVehicles())
                .build();
    }
//
    @GetMapping("/{id}")
    public ApiResponse<VehicleResponse> getVehicleById(@PathVariable Long id) {
        return ApiResponse.<VehicleResponse>builder()
                .result(vehicleService.getById(id))
                .build();
    }
//
    @GetMapping("/user")
    public ApiResponse<List<VehicleResponse>> getUserVehicle(HttpServletRequest request) {
    return ApiResponse.<List<VehicleResponse>>builder()
            .result(vehicleService.getByUser(request))
            .build();
}

    @PostMapping()
    public ApiResponse<VehicleResponse> registerVehicle(
            @RequestBody VehicleRegisterRequest vehicleRegisterRequest,
            HttpServletRequest request)
    {
        return ApiResponse.<VehicleResponse>builder()
                .result(vehicleService.registerVehicle(request, vehicleRegisterRequest))
                .build();
    }

//    @PutMapping
//    public ApiResponse<Vehicle> updateVehicle(@RequestBody VehicleRequest vehicleRequest) {
//
//    }
//
//    @DeleteMapping
//    public ApiResponse<Vehicle> deleteVehicleById(@RequestParam int id) {
//
//    }


}
