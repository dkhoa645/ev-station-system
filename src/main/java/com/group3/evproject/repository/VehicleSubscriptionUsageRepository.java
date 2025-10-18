package com.group3.evproject.repository;

import com.group3.evproject.entity.VehicleSubscriptionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleSubscriptionUsageRepository extends JpaRepository<VehicleSubscriptionUsage,Long> {
}
