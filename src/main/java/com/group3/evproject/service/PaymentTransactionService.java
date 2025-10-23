package com.group3.evproject.service;

import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.repository.PaymentTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentTransactionService {
    PaymentTransactionRepository paymentTransactionRepository;
    VehicleSubscriptionService vehicleSubscriptionService;
    VehicleSubscriptionUsageService vehicleSubscriptionUsageService;
    SubscriptionPlanService subscriptionPlanService;

    public PaymentTransaction savePayment(PaymentTransaction paymentTransaction){
        return paymentTransactionRepository.save(paymentTransaction);
    }

    public PaymentTransaction findById(Long id){
        return paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Transaction "));
    }
    public PaymentTransaction findByRef(String ref){
        return paymentTransactionRepository.findByVnpTxnRef(ref)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"VnpTxnRef"));
    }
    @Transactional
    public void updateTransaction(String ref) {
        PaymentTransaction paymentTransaction = findByRef(ref);
        paymentTransaction.setStatus(PaymentStatusEnum.SUCCESS);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("GMT+7:00"));
        paymentTransaction.setPaidAt(now);

        VehicleSubscription vehicleSubscription = vehicleSubscriptionService
                   .findById(paymentTransaction.getVehicleSubscription().getId());
        vehicleSubscription.setStartDate(now);
        vehicleSubscription.setEndDate(now.plusMonths(1));
        vehicleSubscription.setStatus(VehicleSubscriptionStatusEnum.ACTIVE);
        SubscriptionPlan subscriptionPlan = vehicleSubscription.getSubscriptionPlan();
//                Tao VehicleUsage
        VehicleSubscriptionUsage vehicleSubscriptionUsage = vehicleSubscriptionUsageService.saveUsage(
                VehicleSubscriptionUsage.builder()
                .vehicleSubscription(vehicleSubscription)
                .limitKwh(subscriptionPlan.getLimitValue())
                .usedKwh(BigDecimal.ZERO)
                .build());
    }
}
