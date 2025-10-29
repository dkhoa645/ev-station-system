package com.group3.evproject.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class VehicleBrandResponse {
    Long id;
    String name;
}
