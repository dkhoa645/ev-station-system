package com.group3.evproject.repository;

import com.group3.evproject.controller.SubscriptionPlanController;
import com.group3.evproject.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan,Long> {

}
