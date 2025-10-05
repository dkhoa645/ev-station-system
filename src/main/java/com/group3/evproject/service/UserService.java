package com.group3.evproject.service;

import com.group3.evproject.dto.request.UserCreationRequest;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.Role;
import com.group3.evproject.entity.User;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.UserMapper;
import com.group3.evproject.repository.RoleRepository;
import com.group3.evproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }



    public UserResponse createUser(UserCreationRequest userCreationRequest) {
        User user = userMapper.toUser(userCreationRequest);
        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));

        if(userRepository.existsByUsername(user.getUsername())){
                throw new AppException(ErrorCode.USERNAME_EXISTS);
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse registerUser(UserCreationRequest userCreationRequest) {
        User user = userMapper.toUser(userCreationRequest);
        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));

        Role userRole = roleRepository.findByName("USER");
        if(user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }

        user.getRoles().add(userRole);

        if(userRepository.existsByUsername(user.getUsername())){
            throw new AppException(ErrorCode.USERNAME_EXISTS);
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }
}
