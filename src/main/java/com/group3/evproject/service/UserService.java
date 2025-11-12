package com.group3.evproject.service;

import com.group3.evproject.Enum.RoleName;
import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import com.group3.evproject.dto.request.AdminUserCreationRequest;
import com.group3.evproject.dto.request.CompanyUserCreationRequest;
import com.group3.evproject.dto.request.CompanyUserUpdateRequest;
import com.group3.evproject.dto.request.UserUpdateRequest;
import com.group3.evproject.dto.response.CompanyUserResponse;
import com.group3.evproject.dto.response.UserResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.CompanyMapper;
import com.group3.evproject.mapper.UserMapper;
import com.group3.evproject.mapper.VehicleMapper;
import com.group3.evproject.repository.CompanyRepository;
import com.group3.evproject.repository.UserRepository;
import com.group3.evproject.repository.VehicleRepository;
import com.group3.evproject.utils.PasswordUntil;
import com.group3.evproject.utils.UserUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    VehicleService vehicleService;
    EmailService emailService;
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;

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

    public UserResponse getUserById(@PathVariable Long id) {
        User user =userRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"User"));
        UserResponse userResponse = userMapper.toUserResponse(user);
        Set<RoleName> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        userResponse.setRoles(roles);
        return userResponse;
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

    public void save(User user) {
        userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
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
        if(userUpdateRequest.getName()!=null){
            user.setName(userUpdateRequest.getName());
        }
        if(userUpdateRequest.getPassword()!=null){
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        UserResponse userResponse = userMapper.toUserResponse(userRepository.save(user));
        userResponse.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        return userResponse;
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

    public List<CompanyUserResponse> getAllCompanyUsers() {
        User user = userUtils.getCurrentUser();
                return userRepository
                .findByCompanyAndIdNot(user.getCompany(),user.getId())
                .stream()
                .map( userEach -> {
                    CompanyUserResponse companyUserResponse = userMapper.toCompanyUserResponse(userEach);
                    Vehicle vehicleForResponse = userEach.getVehicles().stream().findFirst()
                            .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle"));
                    companyUserResponse.setVehicleResponse(vehicleMapper.vehicleToVehicleResponse(vehicleForResponse));
                    return companyUserResponse;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CompanyUserResponse createCompanyUser(CompanyUserCreationRequest userCreationRequest) {
        if(userRepository.existsByEmail(userCreationRequest.getEmail())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS, "Email");
        }

        String password = PasswordUntil.generateSecurePassword(10);

        User userCurrent = userUtils.getCurrentUser();
        Company company = userCurrent.getCompany();

        Role role = roleService.findByName(RoleName.DRIVER);
        Set<Role>roles = new HashSet<>();
        roles.add(role);

        Vehicle vehicle = vehicleService.findById(userCreationRequest.getVehicleId());
        if(vehicle.getUser()!=null)
            throw new AppException(ErrorCode.VEHICLE_REGISTED);
        VehicleSubscription vehicleSubscription = vehicle.getSubscription();
        vehicleSubscription.setStatus(VehicleSubscriptionStatus.ACTIVE);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        vehicleSubscription.setStartDate(now);
        vehicleSubscription.setEndDate(now.plusYears(1));

        List<Vehicle> vehicles = new ArrayList<>();
        vehicles.add(vehicle);

        User user = User.builder()
                .name(userCreationRequest.getName())
                .email(userCreationRequest.getEmail())
                .username(userCreationRequest.getEmail())
                .password(passwordEncoder.encode(password))
                .vehicles(vehicles)
                .roles(roles)
                .company(company)
                .verified(true)
                .build();
        vehicle.setUser(user);
        emailService.sendDriverEmail(userCreationRequest.getEmail(),password,company.getName());
        userRepository.save(user);

        CompanyUserResponse companyUserResponse = userMapper.toCompanyUserResponse(user);
        Vehicle vehicleForResponse = vehicles.stream().findFirst().get();
        companyUserResponse.setVehicleResponse(vehicleMapper.vehicleToVehicleResponse(vehicleForResponse));

        return companyUserResponse;
    }


    public String deleteCompanyUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "User"));
        user.getVehicles().stream().forEach(vehicle -> {
            vehicle.setUser(null);
            VehicleSubscription vehicleSubscription = vehicle.getSubscription();
            vehicleSubscription.setStatus(VehicleSubscriptionStatus.PENDING);
            vehicleSubscription.setEndDate(null);
            vehicleSubscription.setStartDate(null);
            vehicleRepository.save(vehicle);
        });
        user.getVehicles().clear();
        userRepository.deleteById(userId);
        return "User has been deleted successfully";
    }

    public UserResponse updateCompanyPass(CompanyUserUpdateRequest companyUserUpdateRequest) {
        User user = userUtils.getCurrentUser();
        user.setPassword(passwordEncoder.encode(companyUserUpdateRequest.getPassword()));
        userRepository.save(user);
        UserResponse userResponse = userMapper.toUserResponse(userRepository.save(user));
        Set<RoleName> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        userResponse.setRoles(roles);
        return userResponse;
    }
}
