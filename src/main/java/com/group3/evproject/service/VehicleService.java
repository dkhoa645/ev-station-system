package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleRequest;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.entity.User;
import com.group3.evproject.entity.Vehicle;
import com.group3.evproject.entity.VehicleModel;
import com.group3.evproject.entity.VehicleStatus;
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

    public List<Vehicle> getAllVehicles() {
        return  vehicleRepository.findAll();
    }
//
    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle"));
        }

    public VehicleResponse registerVehicle(HttpServletRequest request, VehicleRequest vehicleRequest) {
        if(vehicleRepository.existsByLicensePlate(vehicleRequest.getLicensePlate())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Vehicle");
        }
        VehicleModel vehicleModel = vehicleModelService.getModelById(vehicleRequest.getModelId());

        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);

        return vehicleMapper.vehicleToVehicleResponse(
                vehicleRepository.save(
                    Vehicle.builder()
                        .model(vehicleModel)
                        .licensePlate(vehicleRequest.getLicensePlate())
                        .user(user)
                        .status(VehicleStatus.PENDING)
                        .build()));
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
