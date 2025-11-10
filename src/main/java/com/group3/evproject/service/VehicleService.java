package com.group3.evproject.service;

import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import com.group3.evproject.dto.request.CompanyVehicleCreationRequest;
import com.group3.evproject.dto.request.VehicleRegisterRequest;
import com.group3.evproject.dto.response.VehicleResponse;
import com.group3.evproject.dto.response.VehicleSubscriptionResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.SubscriptionPlanMapper;
import com.group3.evproject.mapper.VehicleMapper;
import com.group3.evproject.mapper.VehicleModelMapper;
import com.group3.evproject.mapper.VehicleSubscriptionMapper;
import com.group3.evproject.repository.VehicleRepository;
import com.group3.evproject.repository.VehicleSubscriptionRepository;
import com.group3.evproject.utils.UserUtils;
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
    VehicleModelService vehicleModelService;
    SubscriptionPlanService subscriptionPlanService;
    VehicleSubscriptionRepository vehicleSubscriptionRepository;
    VehicleMapper vehicleMapper;
    VehicleSubscriptionMapper vehicleSubscriptionMapper;
    UserUtils userUtils;

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(vehicleMapper::vehicleToVehicleResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponse getById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle"));
        VehicleResponse response =
                vehicleMapper.vehicleToVehicleResponse(vehicle);
        return  response;
        }

    @Transactional
    public VehicleResponse registerVehicle(
            VehicleRegisterRequest vehicleRegisterRequest) {
//        Kiểm tra hoá don xe da tung duoc thanh toan chua

//         Kiểm tra biển số xe
        if(vehicleRepository.existsByLicensePlate(vehicleRegisterRequest.getLicensePlate())){
            throw new AppException(ErrorCode.RESOURCES_EXISTS,"Vehicle");
        }
//        Tìm Model
        VehicleModel vehicleModel =
                vehicleModelService.getModelById(vehicleRegisterRequest.getModelId());
//         Lấy user để đăng ký xe
        User user = userUtils.getCurrentUser();
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
        VehicleSubscription vehicleSubscription =vehicleSubscriptionRepository.save(
                VehicleSubscription.builder()
                        .vehicle(vehicle)
                        .subscriptionPlan(subscriptionPlan)
                        .startDate(null)
                        .endDate(null)
                        .status(VehicleSubscriptionStatus.PENDING)
                        .autoRenew(true)
                        .paymentTransactions(new ArrayList<>())
                        .build());
//        4.Tạo Response
        VehicleSubscriptionResponse vehicleSubscriptionResponse =
                vehicleSubscriptionMapper.toVehicleSubscriptionResponse(vehicleSubscription);
//        vehicleSubscriptionResponse.setSubscriptionPlanResponse(
//                subscriptionPlanMapper.toSubscriptionPlanResponse(subscriptionPlan));

        VehicleResponse vehicleResponse = vehicleMapper.vehicleToVehicleResponse(vehicle);
        vehicleResponse.setVehicleSubscriptionResponse(vehicleSubscriptionResponse);
//        vehicleResponse.setModel(vehicleModelMapper.toVehicleModelResponse(vehicleModel));
        return vehicleResponse;
    }

    //Vehicle Response
    public List<VehicleResponse> getByUser() {
        User user = userUtils.getCurrentUser();
        List<Vehicle> list = vehicleRepository.findByUser(user);
        List<VehicleResponse> responses = new ArrayList<>();
        for(Vehicle vehicle : list) {
            VehicleResponse response = vehicleMapper.vehicleToVehicleResponse(vehicle);;
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public String deleteByUserAndId(Long id) {
        Vehicle existVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "Vehicle"));

        vehicleRepository.delete(existVehicle);
        return "Vehicle has been deleted";
    }

    public List<VehicleResponse> getCompanyVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(vehicleMapper::vehicleToVehicleResponse)
                .collect(Collectors.toList());
    }

    public Vehicle findById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle"));
    }

    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public VehicleResponse createCompanyVehicle(CompanyVehicleCreationRequest companyVehicleCreationRequest) {


        SubscriptionPlan subscriptionPlan = subscriptionPlanService.findByName("COMPANY");

        VehicleSubscription vehicleSubscription = VehicleSubscription.builder()
                .subscriptionPlan(subscriptionPlan)
                .status(VehicleSubscriptionStatus.PENDING)
                .autoRenew(true)
                .startDate(null)
                .endDate(null)
                .build();

        User user = userUtils.getCurrentUser();

        Vehicle vehicle = Vehicle.builder()
                .company(user.getCompany())
                .licensePlate(companyVehicleCreationRequest.getLicensePlate())
                .model(vehicleModelService.getModelById(companyVehicleCreationRequest.getModelId()))
                .subscription(vehicleSubscription)
                .build();

        vehicleSubscription.setVehicle(vehicle);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return vehicleMapper.vehicleToVehicleResponse(savedVehicle);
    }


    public List<VehicleResponse> getAllCompanyVehicles() {
        User user = userUtils.getCurrentUser();
        List<Vehicle> list = vehicleRepository.findByCompany(user.getCompany());
        return  list.stream()
                .map(vehicleMapper::vehicleToVehicleResponse)
                .collect(Collectors.toList());
    }
}
