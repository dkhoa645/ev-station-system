package com.group3.evproject.service;

import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.repository.VehicleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleService  {
    VehicleRepository vehicleRepository;

    public List<Vehicle> getVehicles() {
        return  vehicleRepository.findAll();
    }
}
