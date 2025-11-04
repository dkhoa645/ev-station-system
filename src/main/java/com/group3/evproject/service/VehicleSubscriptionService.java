package com.group3.evproject.service;

import com.group3.evproject.entity.User;
import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.entity.VehicleSubscription;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.repository.VehicleSubscriptionRepository;
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
    AuthenticationService authenticationService;
    UserService userService;

    public VehicleSubscription findById(Long id){
        return vehicleSubscriptionRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle Subscription"));
    }

    @Transactional
    public VehicleSubscription saveVehicle(VehicleSubscription vehicleSubscription){
        return vehicleSubscriptionRepository
                .save(vehicleSubscription);
    }

    public User isFromUser(HttpServletRequest request,Long id){
        VehicleSubscription vs = findById(id);
        Vehicle vehicle = vs.getVehicle();
        String username = authenticationService.extractUsernameFromRequest(request);
        if(vehicle.getUser().getUsername().equals(username))
            return userService.getUserByUsername(username);
        return null;
    }
}
