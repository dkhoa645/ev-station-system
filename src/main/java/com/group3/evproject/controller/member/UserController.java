package com.group3.evproject.controller.member;

import com.group3.evproject.dto.request.UserCreationRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.User;
import com.group3.evproject.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/member/user")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PutMapping()
    ApiResponse<UserResponse> updateUser(@RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMember(userUpdateRequest))
                .build();
    }

    @DeleteMapping("/{userId}")
    ApiResponse<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }

}
