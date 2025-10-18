package com.group3.evproject.mapper;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.entity.VehicleModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VehicleModelMapper {
    VehicleModel toVehicleModel(VehicleModelRequest vehicleModelRequest);
}
