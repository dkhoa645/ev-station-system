package com.group3.evproject.mapper;

import com.group3.evproject.dto.request.VehicleModelRequest;
import com.group3.evproject.dto.response.VehicleModelResponse;
import com.group3.evproject.entity.VehicleModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VehicleModelMapper {
    VehicleModel toVehicleModel(VehicleModelRequest vehicleModelRequest);

    @Mapping(source = "brand.name", target = "brandName")
    VehicleModelResponse toVehicleModelResponse(VehicleModel vehicleModel);

    @Mapping(target = "brand",ignore = true)
    void updateToVehicleModel(VehicleModelRequest vehicleModelRequest, @MappingTarget VehicleModel vehicleModel);
}
