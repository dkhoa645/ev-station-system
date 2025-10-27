package com.group3.evproject.service;

import com.group3.evproject.dto.request.VehicleRegisterRequest;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.dto.response.VehicleSubscriptionResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.SubscriptionPlanMapper;
import com.group3.evproject.mapper.VehicleMapper;
import com.group3.evproject.mapper.VehicleSubscriptionMapper;
import com.group3.evproject.repository.SubscriptionPlanRepository;
import com.group3.evproject.repository.VehicleRepository;
import com.group3.evproject.repository.VehicleSubscriptionRepository;
import com.group3.evproject.vnpay.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleService  {
    VehicleRepository vehicleRepository;
    AuthenticationService authenticationService;
    UserService userService;
    VehicleMapper vehicleMapper;
    VehicleModelService vehicleModelService;
    SubscriptionPlanService subscriptionPlanService;
    VehicleSubscriptionService vehicleSubscriptionService;
    SubscriptionPlanMapper subscriptionPlanMapper;
    VehicleSubscriptionMapper vehicleSubscriptionMapper;

    public List<VehicleResponse> getAllVehicles() {
          return vehicleRepository.findAll().stream()
                .map(vehicleMapper::vehicleToVehicleResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponse getById(Long id) {
        return  vehicleMapper.vehicleToVehicleResponse(vehicleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle")));
        }

    @Transactional
    public VehicleResponse registerVehicle(HttpServletRequest request, VehicleRegisterRequest vehicleRegisterRequest) {
//        Kiểm tra hoá don xe da tung duoc thanh toan chua

//                Kiểm tra biển số xe
        if(vehicleRepository.existsByLicensePlate(vehicleRegisterRequest.getLicensePlate())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Vehicle");
        }
//        Tìm Model
        VehicleModel vehicleModel =
                vehicleModelService.getModelById(vehicleRegisterRequest.getModelId());
//            Lấy user để đăng ký xe
        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
//        1.Tạo Vehicle, save nên có id
        Vehicle vehicle = vehicleRepository.save(
                Vehicle.builder()
                        .model(vehicleModel)
                        .licensePlate(vehicleRegisterRequest.getLicensePlate())
                        .user(user)
                        .build());
//          Tìm gói từ request
        SubscriptionPlan subscriptionPlan = subscriptionPlanService
                .findById(vehicleRegisterRequest.getSubscriptionPlanId());
//        2.Tạo Vehicle Subscription, save nên có id
        VehicleSubscription vehicleSubscription =vehicleSubscriptionService.saveVehicle(
                VehicleSubscription.builder()
                        .vehicle(vehicle)
                        .subscriptionPlan(subscriptionPlan)
                        .startDate(null)
                        .endDate(null)
                        .status(VehicleSubscriptionStatusEnum.PENDING)
                        .autoRenew(false)
                        .paymentTransactions(new ArrayList<>())
                        .build());
//        4.Tạo Response
        VehicleSubscriptionResponse vehicleSubscriptionResponse =
                vehicleSubscriptionMapper.toVehicleSubscriptionResponse(vehicleSubscription);
        vehicleSubscriptionResponse.setSubscriptionPlanResponse(
                subscriptionPlanMapper.toSubscriptionPlanResponse(subscriptionPlan));

        VehicleResponse vehicleResponse = vehicleMapper.vehicleToVehicleResponse(vehicle);
        vehicleResponse.setVehicleSubscriptionResponse(vehicleSubscriptionResponse);

        return vehicleResponse;
    }

    //Vehicle Response
    public List<VehicleResponse> getByUser(HttpServletRequest request) {
        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
        List<Vehicle> list = vehicleRepository.findByUser(user);
        List<VehicleResponse> responses = new ArrayList<>();
        for(Vehicle vehicle : list) {
            VehicleResponse response = vehicleMapper.vehicleToVehicleResponse(vehicle);
            VehicleSubscription vs = vehicle.getSubscription();
            SubscriptionPlan sp = vs.getSubscriptionPlan();
            VehicleSubscriptionResponse vsr = vehicleSubscriptionMapper.toVehicleSubscriptionResponse(vs);
            vsr.setSubscriptionPlanResponse(
                    subscriptionPlanMapper.toSubscriptionPlanResponse(sp));
            response.setVehicleSubscriptionResponse(vsr);
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public String deleteByUserAndId(Long id, HttpServletRequest request) {
        Vehicle existVehicle = vehicleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle"));

        String username = authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
        List<Vehicle> list = vehicleRepository.findByUser(user);

        for(Vehicle vehicle : list){
            if(vehicle.getId().equals(existVehicle.getId())){
                vehicleRepository.delete(vehicle);
                String message = "Vehicle has been deleted";
                return message;
            }
        }
        throw new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle");
    }


//    @Transactional
//    public VehicleResponse updateUserVehicle(Long id, HttpServletRequest request) {
//        Vehicle vehicle = vehicleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle"));;
//        List<VehicleResponse> vehicleResponseList = getByUser(request);
//    }
}
