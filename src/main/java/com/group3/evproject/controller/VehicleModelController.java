package com.group3.evproject.controller;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.service.VehicleBrandService;
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
    VehicleBrandService vehicleBrandService;


    @GetMapping("/all")
    public ApiResponse<List<VehicleModel>> getVehicleModel() {
        return ApiResponse.<List<VehicleModel>>builder()
                .result(vehicleModelService.getAllModel())
                .build();
    }

    @GetMapping("branch/{id}")
    public ApiResponse<List<VehicleModel>> getVehicleModelByBranch(
            @RequestParam long id) {
        return ApiResponse.<List<VehicleModel>>builder()
                .result(vehicleModelService.getModelByBranch(id))
                .build();
    }

    @PostMapping()
    public ApiResponse<VehicleModel> addVehicleModel(@RequestBody VehicleModelRequest vehicleModelRequest) {
        return ApiResponse.<VehicleModel>builder()
                .result(vehicleModelService.saveModel(vehicleModelRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteVehicleModel(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(vehicleModelService.deleteById(id))
                .build();
    }

//    @GetMapping()
//    public ApiResponse<List<VehicleModel>> searchVehicles(
//            @RequestParam(required = false) String brand,
//            @RequestParam(required = false) String model) {
//        List<VehicleModel> vehicles = vehicleModelService.getByBrandAndName(brand,model);
//        return ApiResponse.<List<VehicleModel>>builder()
//                .result(vehicles)
//                .build();
//    }
//
//    @PostMapping("/bulk")
//    public ApiResponse<List<VehicleModel>> addVehicleModels(@RequestBody List<VehicleModelRequest> models) {
//        List<VehicleModel> savedModels = vehicleModelService.saveAllModel(models);
//        return ApiResponse.<List<VehicleModel>>builder()
//                .result(savedModels)
//                .build();
//    }
//
//    @PostMapping()
//    public ApiResponse<VehicleModel> addVehicleModel(@RequestBody VehicleModelRequest vmr){
//        return ApiResponse.<VehicleModel>builder()
//                .result(vehicleModelService.saveModel(vmr))
//                .build();
//    }
}
