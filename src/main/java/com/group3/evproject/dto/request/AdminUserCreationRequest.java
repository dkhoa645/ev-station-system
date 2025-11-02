package com.group3.evproject.dto.request;

import com.group3.evproject.Enum.RoleName;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUserCreationRequest {
    String email;
    String password;
    String name;
    RoleName role;
    Long companyId;
}
