package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleBrandRequest;
import com.group3.evproject.dto.response.VehicleBrandResponse;
import com.group3.evproject.entity.VehicleBrand;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.VehicleBrandMapper;
import com.group3.evproject.repository.VehicleBranchRepository;
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
public class VehicleBrandService {
    VehicleBranchRepository vehicleBranchRepository;
    VehicleBrandMapper vehicleBrandMapper;

    public List<VehicleBrandResponse> findAll() {
        List<VehicleBrand> vehicleBrands = vehicleBranchRepository.findAll();
        List<VehicleBrandResponse> response = vehicleBrands.stream()
                .map(vehicleBrandMapper::toDto)
                .collect(Collectors.toList());
        return response;
    }

    public VehicleBrand findById(long id) {
        return vehicleBranchRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_EXISTS,"Branch"));
    }

    public VehicleBrand createbrand(VehicleBrandRequest vehicleBrandRequest) {
        VehicleBrand vehicleBrand = new VehicleBrand();
        vehicleBrand.setName(vehicleBrandRequest.getName().toUpperCase());
        return vehicleBranchRepository.save(vehicleBrand);
    }

    public String deleteById(long id) {
        VehicleBrand vehicleBrand = findById(id);
        vehicleBranchRepository.deleteById(id);
        return "Delete " +vehicleBrand.getName() + " successfully";
    }
}
