package com.group3.evproject.controller;

import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.entity.Role;
import com.group3.evproject.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleController {
    RoleService roleService;


//    @PostMapping()
//    public ApiResponse<Role> createRole(){
//        return ApiResponse.<Role>builder()
//                .result(roleService.createRole())
//                .build();
//    }
}
