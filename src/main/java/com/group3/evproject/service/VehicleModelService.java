package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.dto.response.VehicleModelResponse;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.VehicleBrand;
import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.mapper.VehicleModelMapper;
import com.group3.evproject.repository.VehicleModelRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

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



    public VehicleModel saveModel(VehicleModelRequest vmr) {
        VehicleBrand vehicleBrand = vehicleBrandService.findById(vmr.getBrandId());
        VehicleModel vehicleModel = vehicleModelMapper.toVehicleModel(vmr);
        vehicleModel.setBrand(vehicleBrand);
         return vehicleModelRepository.save(vehicleModel);
    }

    public List<VehicleModel> getModelByBranch(long id) {
        VehicleBrand vehicleBrand = vehicleBrandService.findById(id);
        List<VehicleModel> vehicleModels = vehicleModelRepository.findByBrand(vehicleBrand);

        return vehicleModels;
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
}
