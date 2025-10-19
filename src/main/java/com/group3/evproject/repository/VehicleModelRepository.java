package com.group3.evproject.repository;

import com.group3.evproject.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleModelRepository extends JpaRepository<VehicleModel, Long> {
    List<VehicleModel> findByBrandAndModelName(String brand, String model);

    List<VehicleModel> findByModelName(String model);

    List<VehicleModel> findByBrand(String brand);
}
