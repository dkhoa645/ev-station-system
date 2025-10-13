package com.group3.evproject.dto.response;

import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.entity.VehicleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.ui.Model;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VehicleResponse {
    Long id;
    String licensePlate;
    VehicleModel model;
    VehicleStatus status;
}
