package com.group3.evproject.service;

import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import com.group3.evproject.dto.request.SubscriptionRequest;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.User;
import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.entity.VehicleSubscription;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.VehicleMapper;
import com.group3.evproject.repository.VehicleSubscriptionRepository;
import com.group3.evproject.utils.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleSubscriptionService {
    VehicleSubscriptionRepository vehicleSubscriptionRepository;
    UserService userService;
    SubscriptionPlanService subscriptionPlanService;
    private final VehicleService vehicleService;
    VehicleMapper vehicleMapper;
    private final UserUtils userUtils;

    public VehicleSubscription findById(Long id){
        return vehicleSubscriptionRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle Subscription"));
    }

    @Transactional
    public VehicleSubscription saveVehicleSubscription(VehicleSubscription vehicleSubscription){
        return vehicleSubscriptionRepository
                .save(vehicleSubscription);
    }

    public User isFromUser(Long id){
        VehicleSubscription vs = findById(id);
        Vehicle vehicle = vs.getVehicle();
        User user = userUtils.getCurrentUser();
        String username = user.getUsername();
        if(vehicle.getUser().getUsername().equals(username))
            return userService.getUserByUsername(username);
        return null;
    }

    @Transactional
    public VehicleResponse renewSubscription(SubscriptionRequest subscriptionRequest) {
        Vehicle vehicle = vehicleService.findById(subscriptionRequest.getVehicleId());
        if (vehicle.getSubscription() != null) {
            vehicle.getSubscription().setVehicle(null);
            vehicle.setSubscription(null);
        }
        VehicleSubscription vehicleSubscription = vehicleSubscriptionRepository.save(VehicleSubscription.builder()
                .subscriptionPlan(subscriptionPlanService.findById(subscriptionRequest.getSubscriptionId()))
                .vehicle(vehicle)
                .autoRenew(true)
                .startDate(null)
                .endDate(null)
                .status(VehicleSubscriptionStatus.PENDING)
                .build());
        vehicle.setSubscription(vehicleSubscription);
        return vehicleMapper.vehicleToVehicleResponse(vehicleService.saveVehicle(vehicle));
    }

    @Transactional
    public VehicleResponse expiredSubscription(Long vehicleId) {
        Vehicle vehicle = vehicleService.findById(vehicleId);
        vehicle.getSubscription().setStatus(VehicleSubscriptionStatus.EXPIRED);
        return vehicleMapper.vehicleToVehicleResponse(vehicleService.saveVehicle(vehicle));
    }

    public String noRenewSubscription(Long vehicleId) {
        Vehicle vehicle = vehicleService.findById(vehicleId);
        vehicle.getSubscription().setAutoRenew(false);
        vehicleSubscriptionRepository.save(vehicle.getSubscription());
        return "Vehicle no longer renewed subscription";
    }
}
