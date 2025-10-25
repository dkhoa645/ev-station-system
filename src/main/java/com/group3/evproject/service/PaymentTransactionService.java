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


    public String processSuccessfulPayment(String ref) {
//        Tìm transaction có mã giao dịch ref
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByVnpTxnRef(ref)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"VnpTxnRef"));;
        paymentTransaction.setStatus(PaymentStatusEnum.SUCCESS);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        VehicleSubscription vehicleSubscription = paymentTransaction.getVehicleSubscription();
        if(vehicleSubscription == null) throw new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Vehicle subscription");
        vehicleSubscription.setStartDate(now);
        vehicleSubscription.setEndDate(now.plusMonths(1));
        vehicleSubscription.setStatus(VehicleSubscriptionStatusEnum.ACTIVE);

        SubscriptionPlan subscriptionPlan =vehicleSubscription.getSubscriptionPlan();
        if(subscriptionPlan == null) throw new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Subscription plan");
//                Tao VehicleUsage
        VehicleSubscriptionUsage vehicleSubscriptionUsage = VehicleSubscriptionUsage.builder()
                .vehicleSubscription(vehicleSubscription)
                .limitKwh(subscriptionPlan.getLimitValue())
                .usedKwh(BigDecimal.ZERO)
                .build();
        vehicleSubscriptionService.saveVehicle(vehicleSubscription);
        savePayment(paymentTransaction);
        return "Success";
    }

//@Transactional
//public String processSuccessfulPayment(String vnpTxnRef) {
//
//    // ✅ Tìm payment bằng vnpTxnRef
//    PaymentTransaction paymentTransaction = paymentTransactionRepository
//            .findByVnpTxnRef(vnpTxnRef)
//            .orElseThrow(() -> new AppException(
//                    ErrorCode.RESOURCES_NOT_EXISTS,
//                    "Payment transaction with ref: " + vnpTxnRef
//            ));
//
//    // ✅ Kiểm tra trạng thái để tránh xử lý trùng
//    if (paymentTransaction.getStatus() == PaymentStatusEnum.SUCCESS) {
//        return "Already processed";
//    }
//
//    VehicleSubscription vehicleSubscription = paymentTransaction.getVehicleSubscription();
//    if (vehicleSubscription == null) {
//        throw new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "Vehicle subscription");
//    }
//
//    SubscriptionPlan subscriptionPlan = vehicleSubscription.getSubscriptionPlan();
//    if (subscriptionPlan == null) {
//        throw new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "Subscription plan");
//    }
//
//    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
//
//    // ✅ Cập nhật payment
//    paymentTransaction.setStatus(PaymentStatusEnum.SUCCESS);
//    paymentTransaction.setPaidAt(now);
//
//    // ✅ Cập nhật hoặc kích hoạt subscription
//    if (vehicleSubscription.getStatus() == VehicleSubscriptionStatusEnum.PENDING
//            || vehicleSubscription.getStartDate() == null) {
//        // Lần đầu kích hoạt
//        vehicleSubscription.setStartDate(now);
//        vehicleSubscription.setEndDate(now.plusMonths(1));
//    } else {
//        // Gia hạn - extend từ endDate hiện tại
//        LocalDateTime currentEnd = vehicleSubscription.getEndDate();
//        vehicleSubscription.setEndDate(currentEnd.plusMonths(1));
//    }
//    vehicleSubscription.setStatus(VehicleSubscriptionStatusEnum.ACTIVE);
//
//    // ✅ Xử lý usage
//    VehicleSubscriptionUsage usage = vehicleSubscription.getUsage();
//    if (usage == null) {
//        usage = VehicleSubscriptionUsage.builder()
//                .vehicleSubscription(vehicleSubscription)
//                .limitKwh(subscriptionPlan.getLimitValue())
//                .usedKwh(BigDecimal.ZERO)
//                .build();
//        vehicleSubscription.setUsage(usage);
//    } else {
//        // Reset usage cho chu kỳ mới
//        usage.setLimitKwh(subscriptionPlan.getLimitValue());
//        usage.setUsedKwh(BigDecimal.ZERO);
//    }
//
//    // ✅ Save parent entity (cascade sẽ save các child)
//    vehicleSubscriptionService.saveVehicle(vehicleSubscription);
//    paymentTransactionRepository.save(paymentTransaction);
//
//    return "Success";
//}
}
