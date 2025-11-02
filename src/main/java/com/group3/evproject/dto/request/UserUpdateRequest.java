package com.group3.evproject.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;
    @Size(min=1, message = "Name must be at lease 1 character")
    String name;

}
