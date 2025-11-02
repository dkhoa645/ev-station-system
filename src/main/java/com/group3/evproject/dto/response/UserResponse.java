package com.group3.evproject.dto.response;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String email;
    String username;
    String name;
    String message;
    HashSet<RoleName> roles;
}
