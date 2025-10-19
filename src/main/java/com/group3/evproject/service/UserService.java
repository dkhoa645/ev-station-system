package com.group3.evproject.service;

import com.group3.evproject.dto.request.UserCreationRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.Role;
import com.group3.evproject.entity.User;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.UserMapper;
import com.group3.evproject.repository.RoleRepository;
import com.group3.evproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


    @Transactional
    public UserResponse createUser(UserCreationRequest userCreationRequest) {
        User user = userMapper.toUser(userCreationRequest);
        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
        if(userRepository.existsByUsername(user.getUsername())){
                throw new AppException(ErrorCode.RESOURCES_EXISTS,"User");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS, "Email");
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }

//    @Transactional
//    public UserResponse registerUser(UserCreationRequest userCreationRequest) {
//        User user = userMapper.toUser(userCreationRequest);
//        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
//
//        Role userRole = roleRepository.findByName("USER");
//        if(user.getRoles() == null) {
//            user.setRoles(new HashSet<>());
//        }
//
//        user.getRoles().add(userRole);
//
//        if(userRepository.existsByUsername(user.getUsername())){
//            throw new AppException(ErrorCode.RESOURCES_EXISTS,"User");
//        }
//        if(userRepository.existsByEmail(user.getEmail())){
//            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Email");
//        }
//        return userMapper.toUserResponse(userRepository.save(user));
//    }

    public UserResponse getUserById(@PathVariable Long id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"User")));
    }

    //ADMIN
    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "User"));
        userMapper.updateUserFromRequest(userUpdateRequest, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        if(userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        }
        else {
            throw new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "User");
        }
    }
}
