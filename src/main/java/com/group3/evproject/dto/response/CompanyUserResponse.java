package com.group3.evproject.dto.response;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.entity.Vehicle;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyUserResponse {
    Long id;
    String email;
    String username;
    String name;
    List<Vehicle> vehicles;

}
