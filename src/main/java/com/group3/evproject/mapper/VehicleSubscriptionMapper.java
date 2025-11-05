package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.VehicleSubscriptionResponse;
import com.group3.evproject.entity.VehicleSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SubscriptionPlanMapper.class})
public interface VehicleSubscriptionMapper {
    @Mapping(source = "subscriptionPlan", target = "subscriptionPlanResponse")
    VehicleSubscriptionResponse toVehicleSubscriptionResponse(VehicleSubscription vehicleSubscription);
}
