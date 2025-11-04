package com.group3.evproject.repository;
import com.group3.evproject.entity.ChargingStation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    List<ChargingStation> findByStatus(String status);
    List<ChargingStation> findByLocationContainingIgnoreCase(String location);

    @EntityGraph(attributePaths = {"spots"})
    List<ChargingStation> findAll();

    @EntityGraph(attributePaths = {"spots"})
    Optional<ChargingStation> findById(Long id);

}
