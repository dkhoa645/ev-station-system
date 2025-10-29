package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleModelRequest;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleModelService {
    VehicleModelRepository vehicleModelRepository;
    VehicleModelMapper vehicleModelMapper;
    VehicleBranchService vehicleBranchService;

    public List<VehicleModel> getAllModel() {
        return vehicleModelRepository.findAll();
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
        VehicleBrand vehicleBrand = vehicleBranchService.findById(vmr.getBrandId());
        VehicleModel vehicleModel = vehicleModelRepository.save(vehicleModelMapper.toVehicleModel(vmr));
        vehicleModel.setBrand(vehicleBrand);
         return vehicleModel;
    }

    public List<VehicleModel> getModelByBranch(long id) {
        VehicleBrand vehicleBrand = vehicleBranchService.findById(id);
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
}
