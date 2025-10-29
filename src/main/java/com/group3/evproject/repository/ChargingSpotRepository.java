package com.group3.evproject.repository;

import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChargingSpotRepository extends JpaRepository<ChargingSpot, Long> {
    List<ChargingSpot> findByStationId(Long stationId);
    List<ChargingSpot> findByStatus(ChargingSpot.SpotStatus status);
    Optional<ChargingSpot> findFirstByStationAndStatus(ChargingStation station, ChargingSpot.SpotStatus status);
}
