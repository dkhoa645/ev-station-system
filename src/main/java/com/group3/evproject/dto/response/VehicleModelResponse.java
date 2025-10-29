package com.group3.evproject.dto.response;

import com.group3.evproject.entity.VehicleBrand;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleModelResponse {
    Long id;
    String brandName;
    String modelName;
    String connector;
    Double batteryCapacity;
    String url;
}
