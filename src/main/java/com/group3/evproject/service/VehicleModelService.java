package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.mapper.VehicleModelMapper;
import com.group3.evproject.repository.VehicleModelRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleModelService {
    VehicleModelRepository vehicleModelRepository;
    VehicleModelMapper vehicleModelMapper;

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

    public List<VehicleModel> getByBrandAndName(String brand, String model) {
        if(brand!=null && model!=null){
            return vehicleModelRepository.findByBrandAndModelName(brand,model);
        }
        else if(brand!=null ){
            return vehicleModelRepository.findByBrand(brand);
        }else
            return vehicleModelRepository.findByModelName(model);
    }

    public VehicleModel saveModel(VehicleModelRequest vmr) {
         return vehicleModelRepository.save(vehicleModelMapper.toVehicleModel(vmr));
    }

    public String deleteModelById(Long id) {
        vehicleModelRepository.deleteById(id);
        return "Success";
    }
}
