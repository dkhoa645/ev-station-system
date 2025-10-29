package com.group3.evproject.repository;

import com.group3.evproject.entity.ChargingPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargingPointRepository extends JpaRepository<ChargingPoint, Long> {
}
