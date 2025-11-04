package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.VehicleSubscriptionResponse;
import com.group3.evproject.entity.VehicleSubscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleSubscriptionMapper {
    VehicleSubscriptionResponse toVehicleSubscriptionResponse(VehicleSubscription vehicleSubscription);
}
