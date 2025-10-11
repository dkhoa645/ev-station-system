package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleRequest;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.UserMapper;
import com.group3.evproject.mapper.VehicleMapper;
import com.group3.evproject.repository.VehicleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleService  {
    VehicleRepository vehicleRepository;
    VehicleMapper vehicleMapper;

    public List<Vehicle> getVehicles() {
        return  vehicleRepository.findAll();
    }

    public Vehicle findVehicleById(int id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VEHICLE_NOT_EXISTS));
        }

    public VehicleResponse saveVehicle(VehicleRequest vehicleRequest) {
        if(vehicleRepository.existVehicleByLicensePlate(vehicleRequest.getLicensePlate())){
            throw new AppException(ErrorCode.VEHICLE_EXISTS);
        }
        Vehicle vehicle = vehicleMapper.toVehicle(vehicleRequest);
        VehicleResponse response = vehicleMapper.toVehicleResponse(vehicleRepository.save(vehicle));
        return response;
    }
}
