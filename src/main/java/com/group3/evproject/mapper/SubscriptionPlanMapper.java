package com.group3.evproject.mapper;

import com.group3.evproject.dto.request.SubscriptionPlanRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.SubscriptionPlanResponse;
import com.group3.evproject.entity.SubscriptionPlan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanMapper {
    void toSubscriptionPlan(SubscriptionPlanRequest request, @MappingTarget SubscriptionPlan plan);

    SubscriptionPlanResponse toSubscriptionPlanResponse(SubscriptionPlan subscriptionPlan);
}
