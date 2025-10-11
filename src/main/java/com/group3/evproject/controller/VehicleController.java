package com.group3.evproject.controller;

import com.group3.evproject.dto.request.VehicleRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.service.VehicleService;
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
                .result(vehicleService.getVehicles())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<Vehicle> getVehicleById(@PathVariable int id) {
        return ApiResponse.<Vehicle>builder()
                .result(vehicleService.findVehicleById(id))
                .build();
    }

    @PostMapping()
    public ApiResponse<VehicleResponse> saveVehicle(@RequestBody VehicleRequest vehicleRequest) {
        return ApiResponse.<VehicleResponse>builder()
                .result(vehicleService.saveVehicle(vehicleRequest))
                .build();
    }

    @PutMapping
    public ApiResponse<Vehicle> updateVehicle(@RequestBody VehicleRequest vehicleRequest) {

    }

    @DeleteMapping
    public ApiResponse<Vehicle> deleteVehicleById(@RequestParam int id) {

    }


}
