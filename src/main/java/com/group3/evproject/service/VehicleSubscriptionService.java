package com.group3.evproject.service;

import com.group3.evproject.entity.VehicleSubscription;
import com.group3.evproject.repository.VehicleSubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleSubscriptionService {
    VehicleSubscriptionRepository vehicleSubscriptionRepository;

    public VehicleSubscription findById(Long id){
        return vehicleSubscriptionRepository.findById(id)
                .orElse(null);
    }

    @Transactional
    public VehicleSubscription saveVehicle(VehicleSubscription vehicleSubscription){
        return vehicleSubscriptionRepository
                .save(vehicleSubscription);
    }
}
