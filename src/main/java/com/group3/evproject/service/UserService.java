package com.group3.evproject.service;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.dto.request.AdminUserCreationRequest;
import com.group3.evproject.dto.request.CompanyUserCreationRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.CompanyMapper;
import com.group3.evproject.mapper.UserMapper;
import com.group3.evproject.repository.CompanyRepository;
import com.group3.evproject.repository.UserRepository;
import com.group3.evproject.utils.UserUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleService roleService;
    UserUtils userUtils;
    CompanyRepository companyRepository;
    CompanyMapper companyMapper;


    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(entity -> {
                    UserResponse userResponse = userMapper.toUserResponse(entity);
                    Set<RoleName> roles = entity.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
                    userResponse.setRoles(roles);
                    userResponse.setCompanyResponse(companyMapper.toCompanyResponse(entity.getCompany()));
                    return userResponse;
                })
                .collect(Collectors.toList());
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS));
    }


    @Transactional
    public UserResponse createUser(AdminUserCreationRequest userCreationRequest) {
        if(userCreationRequest.getRole().equals(RoleName.ADMIN)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        User user = userMapper.toUserFromAdmin(userCreationRequest);
        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
        if(userRepository.existsByUsername(user.getUsername())){
                throw new AppException(ErrorCode.RESOURCES_EXISTS,"User");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS, "Email");
        }
        Company company = companyRepository.findById(userCreationRequest.getCompanyId())
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Company"));

        Role role = roleService.findByName(userCreationRequest.getRole());
        Set<Role>roles = new HashSet<>();
        roles.add(role);

        user.setCompany(company);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserResponse userResponse = userMapper.toUserResponse(userRepository.save(user));
        userResponse.setCompanyResponse(companyMapper.toCompanyResponse(company));
        userResponse.setRoles(roles.stream().map(Role::getName).collect(Collectors.toSet()));
        return userResponse;
    }

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "User"));

            userRepository.delete(user);
        }


    @Transactional
    public UserResponse updateMember(@Valid UserUpdateRequest userUpdateRequest) {
        User user = userUtils.getCurrentUser();
        userMapper.updateUserFromRequest(userUpdateRequest, user);
        user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public String deleteMember() {
        User user = userUtils.getCurrentUser();
        userRepository.deleteById(user.getId());
        return "Deleted" + user.getId() + "successfully";
    }

    public UserResponse getUserInfo() {
        User user = userUtils.getCurrentUser();
        UserResponse userResponse = userMapper.toUserResponse(user);
        Set<RoleName> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        userResponse.setRoles(roles);
        return userResponse;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<UserResponse> getAllCompanyUsers() {
        User user = userUtils.getCurrentUser();
        return userRepository
                .findByCompanyIdAndIdNot(user.getCompany().getId(),user.getId())
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse createCompanyUser(CompanyUserCreationRequest userCreationRequest) {

        User user = userMapper.toUserFromCompany(userCreationRequest);
        user.setPassword(passwordEncoder.encode(userCreationRequest.getPassword()));
        if(userRepository.existsByUsername(user.getUsername())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"User");
        }
        if(userRepository.existsByEmail(user.getEmail())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS, "Email");
        }

        User userCurrent = userUtils.getCurrentUser();
        Company company = userCurrent.getCompany();

        Role role = roleService.findByName(RoleName.DRIVER);
        Set<Role>roles = new HashSet<>();
        roles.add(role);

        user.setCompany(company);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserResponse userResponse = userMapper.toUserResponse(userRepository.save(user));
        userResponse.setCompanyResponse(companyMapper.toCompanyResponse(company));
        userResponse.setRoles(roles.stream().map(Role::getName).collect(Collectors.toSet()));
        return userResponse;
    }
}
