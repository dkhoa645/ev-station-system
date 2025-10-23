package com.group3.evproject.service;

import com.group3.evproject.entity.VehicleSubscriptionUsage;
import com.group3.evproject.repository.VehicleSubscriptionUsageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleSubscriptionUsageService {
    VehicleSubscriptionUsageRepository vehicleSubscriptionUsageRepository;
    public VehicleSubscriptionUsage saveUsage(VehicleSubscriptionUsage vehicleSubscriptionUsage){
        return vehicleSubscriptionUsageRepository.save(vehicleSubscriptionUsage);
    }
}
