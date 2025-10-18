package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleRegisterRequest;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.SubscriptionPlanMapper;
import com.group3.evproject.mapper.VehicleMapper;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import com.group3.evproject.repository.VehicleRepository;
import com.group3.evproject.repository.VehicleSubscriptionRepository;
import com.group3.evproject.repository.VehicleSubscriptionUsageRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    SubscriptionPlanRepository subscriptionPlanRepository;
    VehicleSubscriptionRepository vehicleSubscriptionRepository;
    SubscriptionPlanService subscriptionPlanService;
    VehicleSubscriptionUsageRepository vehicleSubscriptionUsageRepository;
    SubscriptionPlanMapper subscriptionPlanMapper;

    public List<Vehicle> getAllVehicles() {
        return  vehicleRepository.findAll();
    }
//
    public VehicleResponse getById(Long id) {
        return  vehicleMapper.vehicleToVehicleResponse(vehicleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle")));
        }


    public VehicleResponse registerVehicle(HttpServletRequest request, VehicleRegisterRequest vehicleRegisterRequest) {
        if(vehicleRepository.existsByLicensePlate(vehicleRegisterRequest.getLicensePlate())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Vehicle");
        }
        VehicleModel vehicleModel = vehicleModelService.getModelById(vehicleRegisterRequest.getModelId());

        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
//        Tạo xe
        Vehicle vehicle = vehicleRepository.save(
                Vehicle.builder()
                        .model(vehicleModel)
                        .licensePlate(vehicleRegisterRequest.getLicensePlate())
                        .user(user)
                        .build());
//        Tìm gói từ request
        SubscriptionPlan subscriptionPlan = subscriptionPlanRepository
                .findById(vehicleRegisterRequest.getSubscriptionPlanId())
                .orElse(null);
//        Tạo Vehicle Subscription
        VehicleSubscription vehicleSubscription =vehicleSubscriptionRepository.save(
                VehicleSubscription.builder()
                        .vehicle(vehicle)
                        .subscriptionPlan(subscriptionPlan)
                        .startDate(LocalDateTime.now())
                        .endDate(LocalDateTime.now().plusMonths(1))
                        .status("ACTIVE")
                        .autoRenew(false)
                        .build());
//        Tao VehicleUsage
        VehicleSubscriptionUsage vehicleSubscriptionUsage = vehicleSubscriptionUsageRepository.save(
                VehicleSubscriptionUsage.builder()
                .vehicleSubscription(vehicleSubscription)
                .limitKwh(subscriptionPlan.getLimitValue())
                .usedKwh(BigDecimal.ZERO)
                .resetDate(LocalDateTime.now().plusMonths(1))
                .build());

        VehicleResponse response = vehicleMapper.vehicleToVehicleResponse(vehicle);
        response.setSubscriptionPlanResponse(subscriptionPlanMapper.toSubscriptionPlanResponse(subscriptionPlan));
        return response;
    }

    //Vehicle Response
    public List<VehicleResponse> getByUser(HttpServletRequest request) {
        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
        List<Vehicle> list = vehicleRepository.findByUser(user);
        List<VehicleResponse> responses = new ArrayList<>();
        for(Vehicle vehicle : list){
            VehicleResponse response = vehicleMapper.vehicleToVehicleResponse(vehicle);
            response.setSubscriptionPlanResponse(
                    subscriptionPlanMapper.toSubscriptionPlanResponse(vehicle.getSubscription().getSubscriptionPlan()));

            responses.add(response);
        }

        return responses;
    }


}
