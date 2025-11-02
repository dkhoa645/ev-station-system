package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.dto.response.VehicleModelResponse;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.VehicleBrand;
import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.VehicleModelMapper;
import com.group3.evproject.repository.VehicleModelRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleModelService {
    VehicleModelRepository vehicleModelRepository;
    VehicleModelMapper vehicleModelMapper;
    VehicleBrandService vehicleBrandService;

    public List<VehicleModelResponse> getAllModel() {
        List<VehicleModelResponse>response = vehicleModelRepository.findAll().stream()
                .map(vehicleModelMapper :: toVehicleModelResponse)
                .collect(Collectors.toList());


        return response;
    }

    @Transactional
    public List<VehicleModel> saveAllModel(List<VehicleModelRequest> models) {
        List<VehicleModel> savedModels = models.stream()
                .map(vehicleModelMapper::toVehicleModel)
                .toList();
        return vehicleModelRepository.saveAll(savedModels);
    }

    public VehicleModel getModelById(long id) {
        return vehicleModelRepository.findById(id).orElse(null);
    }



    public VehicleModelResponse saveModel(VehicleModelRequest vmr) {
        VehicleBrand vehicleBrand = vehicleBrandService.findById(vmr.getBrandId());
        VehicleModel vehicleModel = vehicleModelMapper.toVehicleModel(vmr);
        vehicleModel.setBrand(vehicleBrand);
         return vehicleModelMapper.toVehicleModelResponse(vehicleModelRepository.save(vehicleModel));
    }

    public List<VehicleModelResponse> getModelByBrand(long id) {
        VehicleBrand vehicleBrand = vehicleBrandService.findById(id);
        List<VehicleModel> vehicleModels = vehicleModelRepository.findByBrand(vehicleBrand);
        List<VehicleModelResponse> response = vehicleModels.stream()
                .map(vehicleModelMapper::toVehicleModelResponse)
                .collect(Collectors.toList());
        return response;
    }

    public String deleteById(Long id) {
        vehicleModelRepository.deleteById(id);
        return "Success";
    }

    public String deleteModelById(Long id) {
        vehicleModelRepository.deleteById(id);
        return "Success";
    }


    public VehicleModelResponse updateModel(Long id, VehicleModelRequest vehicleModelRequest) {
            VehicleModel vehicleModel = vehicleModelMapper.toVehicleModel(vehicleModelRequest);
            vehicleModel.setId(id);

            VehicleBrand vehicleBrand = vehicleBrandService.findById(id);
            vehicleModel.setBrand(vehicleBrand);
            vehicleModelRepository.save(vehicleModel);
            return vehicleModelMapper.toVehicleModelResponse(vehicleModel);
    }

    public VehicleModelResponse getById(long id) {
        VehicleModel vehicleModel = vehicleModelRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Model"));
        return vehicleModelMapper.toVehicleModelResponse(vehicleModel);
    }
}
