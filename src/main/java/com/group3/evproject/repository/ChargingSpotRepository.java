package com.group3.evproject.repository;
import com.group3.evproject.entity.ChargingSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChargingSpotRepository extends JpaRepository<ChargingSpot, Integer> {
    List<ChargingSpot> findByStationId(Integer stationId);
    List<ChargingSpot> findByStatusIgnoreCase(String status);
}
