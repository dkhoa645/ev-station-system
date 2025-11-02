package com.group3.evproject.controller.admin;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.VehicleModelResponse;
import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.service.VehicleBrandService;
import com.group3.evproject.service.VehicleModelService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/model")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleModelController {
    VehicleModelService vehicleModelService;
    VehicleBrandService vehicleBrandService;


    @GetMapping("/all")
    public ApiResponse<List<VehicleModelResponse>> getVehicleModel() {
        return ApiResponse.<List<VehicleModelResponse>>builder()
                .result(vehicleModelService.getAllModel())
                .build();
    }

    @GetMapping("by-brand")
    public ApiResponse<List<VehicleModelResponse>> getVehicleModelByBranch(
            @RequestParam long id) {
        return ApiResponse.<List<VehicleModelResponse>>builder()
                .result(vehicleModelService.getModelByBrand(id))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<VehicleModelResponse> getVehicleModelById(@PathVariable long id) {
        return ApiResponse.<VehicleModelResponse>builder()
                .result(vehicleModelService.getById(id))
                .build();
    }

    @PostMapping()
    public ApiResponse<VehicleModelResponse> addVehicleModel(@RequestBody VehicleModelRequest vehicleModelRequest) {
        return ApiResponse.<VehicleModelResponse>builder()
                .result(vehicleModelService.saveModel(vehicleModelRequest))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteVehicleModel(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(vehicleModelService.deleteById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<VehicleModelResponse> update(
            @PathVariable Long id,
            @RequestBody VehicleModelRequest vehicleModelRequest) {
        return ApiResponse.<VehicleModelResponse>builder()
                .result(vehicleModelService.updateModel(id,vehicleModelRequest))
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
