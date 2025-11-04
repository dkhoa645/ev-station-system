package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.VehicleBrandResponse;
import com.group3.evproject.entity.VehicleBrand;
import org.mapstruct.Mapper;

import java.awt.*;

@Mapper(componentModel = "spring")
public interface VehicleBrandMapper {
    VehicleBrandResponse toDto(VehicleBrand brand);
}
