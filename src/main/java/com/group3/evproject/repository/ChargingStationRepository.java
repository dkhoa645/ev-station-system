package com.group3.evproject.repository;
import com.group3.evproject.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, Integer> {
    List<ChargingStation> findByStatus(String status);
    List<ChargingStation> findByLocationContainingIgnoreCase(String location);
}
