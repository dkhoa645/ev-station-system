package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleRequest;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.UserMapper;
import com.group3.evproject.mapper.VehicleMapper;
import com.group3.evproject.repository.VehicleRepository;
import com.sendgrid.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleService  {
    VehicleRepository vehicleRepository;
    AuthenticationService authenticationService;
    UserService userService;
    VehicleMapper vehicleMapper;
    VehicleModelService vehicleModelService;
    private final SubscriptionPlanService subscriptionPlanService;

    public List<Vehicle> getAllVehicles() {
        return  vehicleRepository.findAll();
    }
//
    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle"));
        }

    public VehicleResponse registerVehicle(HttpServletRequest request, VehicleRequest vehicleRequest) {
        // Kiểm tra license plate tồn tại
        if(vehicleRepository.existsByLicensePlate(vehicleRequest.getLicensePlate())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Vehicle");
        }

        // Kiểm tra subscription plan
        if(vehicleRequest.getSubscriptionPlanId() == null){
            throw new AppException(ErrorCode.SUBSCRIPTION_REQUIRED);
        }
        SubscriptionPlan plan = subscriptionPlanService.getById(vehicleRequest.getSubscriptionPlanId());

        VehicleModel vehicleModel = vehicleModelService.getModelById(vehicleRequest.getModelId());
        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);

        // Tạo Vehicle + Subscription cùng lúc
        Vehicle vehicle = Vehicle.builder()
                .model(vehicleModel)
                .licensePlate(vehicleRequest.getLicensePlate())
                .user(user)
                .status(VehicleStatus.PENDING)
                .subscription(
                        VehicleSubscription.builder()
                                .subscriptionPlan(plan)
                                .startDate(LocalDateTime.now())
                                .endDate(LocalDateTime.now().plusMonths(1))
                                .status("active")
                                .build()
                )
                .build();

        // Thiết lập quan hệ 2 chiều
        vehicle.getSubscription().setVehicle(vehicle);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return vehicleMapper.vehicleToVehicleResponse(savedVehicle);
    }
    //Vehicle Response
    public List<VehicleResponse> getByUserId(HttpServletRequest request) {
        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
        List<Vehicle> list = vehicleRepository.findByUser(user);
        List<VehicleResponse> responses = new ArrayList<>();
        for(Vehicle vehicle : list){
            responses.add(vehicleMapper.vehicleToVehicleResponse(vehicle));
        }
        return responses;
    }


}
