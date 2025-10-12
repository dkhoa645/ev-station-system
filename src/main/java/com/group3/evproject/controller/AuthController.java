package com.group3.evproject.controller;

import com.group3.evproject.dto.request.AuthenticationRequest;
import com.group3.evproject.dto.request.IntrospectRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.request.UserCreationRequest;
import com.group3.evproject.dto.response.AuthenticationResponse;
import com.group3.evproject.dto.response.IntrospectResponse;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.service.AuthenticationService;
import com.group3.evproject.service.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    UserService userService;
    AuthenticationService authenticationService;


    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticated(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/register")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(authenticationService.registerUser(request))
                .build();
    }

    @GetMapping("/verify")
    public ApiResponse<String> verifyEmail(@RequestParam String token) {
        String message = authenticationService.verifyEmail(token);
        return ApiResponse.<String>builder()
                .result(message)
                .build();
    }


}
