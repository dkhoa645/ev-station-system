package com.group3.evproject.repository;

import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.entity.VehicleSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleSubscriptionRepository extends JpaRepository<VehicleSubscription, Long> {
    Optional<VehicleSubscription> findByVehicleAndStatus(Vehicle vehicle, String status);
    
}
