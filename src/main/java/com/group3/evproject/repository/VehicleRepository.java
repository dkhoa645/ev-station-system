package com.group3.evproject.repository;

import com.group3.evproject.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    boolean existsByLicensePlate(String licensePlate);
}
