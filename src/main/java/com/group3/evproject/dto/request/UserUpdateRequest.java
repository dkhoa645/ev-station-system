package com.group3.evproject.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    String name;


}
