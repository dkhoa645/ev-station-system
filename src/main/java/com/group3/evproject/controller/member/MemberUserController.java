package com.group3.evproject.controller.member;

import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;


@RequestMapping("/member/user")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberUserController {

    UserService userService;

    @GetMapping()
    ApiResponse<UserResponse> getMemberInfo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserInfo())
                .build();
    }

    @PutMapping()
    ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMember(userUpdateRequest))
                .build();
    }

    @DeleteMapping()
    ApiResponse<String> deleteUser() {
        return ApiResponse.<String>builder()
                .result(userService.deleteMember())
                .build();
    }

}
