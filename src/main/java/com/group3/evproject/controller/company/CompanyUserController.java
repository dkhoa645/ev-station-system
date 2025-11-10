package com.group3.evproject.controller.company;

import com.group3.evproject.dto.request.AdminUserCreationRequest;
import com.group3.evproject.dto.request.CompanyUserCreationRequest;
import com.group3.evproject.dto.request.CompanyUserUpdateRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.CompanyResponse;
import com.group3.evproject.dto.response.CompanyUserResponse;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.service.CompanyService;
import com.group3.evproject.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/company/user")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyUserController {

    UserService userService;
    CompanyService companyService;

    @GetMapping("/all")
    ApiResponse<List<CompanyUserResponse>>  getUsers() {
        return ApiResponse.<List<CompanyUserResponse>>builder()
                .result(userService.getAllCompanyUsers())
                .build();
    }

    @GetMapping("/info")
    ApiResponse<CompanyResponse>  getCompanyInfo() {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.getCompanyInfo())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable Long userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @PostMapping
    ApiResponse<CompanyUserResponse> createUser(@RequestBody @Valid CompanyUserCreationRequest request) {
        return ApiResponse.<CompanyUserResponse>builder()
                .result(userService.createCompanyUser(request))
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser
            (@PathVariable Long userId,
             @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, userUpdateRequest))
                .build();
    }

    @PutMapping("/password")
    ApiResponse<UserResponse> updateCompany(CompanyUserUpdateRequest companyUserUpdateRequest) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateCompanyPass(companyUserUpdateRequest))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable Long userId) {
        return ApiResponse.<String>builder()
                .result(userService.deleteCompanyUser(userId))
                .build();
    }

}
