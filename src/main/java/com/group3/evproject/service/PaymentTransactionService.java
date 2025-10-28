package com.group3.evproject.service;

import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.Enum.PaymentTransaction;
import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import com.group3.evproject.dto.response.PaymentTransactionResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.PaymentTransactionMapper;
import com.group3.evproject.mapper.VehicleSubscriptionMapper;
import com.group3.evproject.repository.PaymentTransactionRepository;
import com.group3.evproject.vnpay.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentTransactionService {
    PaymentTransactionRepository paymentTransactionRepository;
    PaymentTransactionMapper paymentTransactionMapper;
    VehicleSubscriptionService vehicleSubscriptionService;
    VehicleSubscriptionMapper vehicleSubscriptionMapper;
    PaymentService paymentService;

    SubscriptionPlanService subscriptionPlanService;

    public com.group3.evproject.entity.PaymentTransaction savePayment(com.group3.evproject.entity.PaymentTransaction paymentTransaction){
        return paymentTransactionRepository.save(paymentTransaction);
    }

    public com.group3.evproject.entity.PaymentTransaction findById(Long id){
        return paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"Transaction "));
    }
    public com.group3.evproject.entity.PaymentTransaction findByRef(String ref){
        return paymentTransactionRepository.findByVnpTxnRef(ref)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"VnpTxnRef"));
    }

    @Transactional
    public PaymentTransactionResponse createSubscriptionPayment(Long id, HttpServletRequest request) {
        VehicleSubscription vehicleSubscription = vehicleSubscriptionService.findById(id);
        SubscriptionPlan subscriptionPlan = vehicleSubscription.getSubscriptionPlan();
        com.group3.evproject.entity.PaymentTransaction paymentTransaction = savePayment(com.group3.evproject.entity.PaymentTransaction.builder()
                .vehicleSubscription(vehicleSubscription)
                .amount(subscriptionPlan.getPrice())
                .paymentMethod("VNPAY")
                .vnpTxnRef(VNPayUtil.getRandomNumber(8))
                .status(PaymentTransaction.PENDING).createdAt(LocalDateTime.now())
                .paidAt(null)
                .bankCode("VNBANK")
                .user(vehicleSubscriptionService.isFromUser(request,id))
                .build());
        vehicleSubscription.getPaymentTransactions().add(paymentTransaction);
        vehicleSubscriptionService.saveVehicle(vehicleSubscription);

        PaymentTransactionResponse response = paymentTransactionMapper.toResponse(paymentTransaction);
        response.setVehicleSubscriptionResponse(vehicleSubscriptionMapper.toVehicleSubscriptionResponse(vehicleSubscription));
        return response;
    }


    @Transactional
    public String processSuccessfulPayment(String ref) {
//        Tìm transaction có mã giao dịch ref
        com.group3.evproject.entity.PaymentTransaction paymentTransaction = paymentTransactionRepository.findByVnpTxnRef(ref)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"VnpTxnRef"));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        paymentTransaction.setPaidAt(now);
        paymentTransaction.setStatus(PaymentTransaction.SUCCESS);
        //            Set gói
        VehicleSubscription vehicleSubscription = paymentTransaction.getVehicleSubscription();
        if (vehicleSubscription == null) {
            throw new AppException(ErrorCode.RESOURCES_NOT_EXISTS, "Vehicle subscription not found");
        }
        // 4. Cập nhật thông tin gói
        vehicleSubscription.setStartDate(now);
        vehicleSubscription.setEndDate(now.plusMonths(1));
        vehicleSubscription.setStatus(VehicleSubscriptionStatus.ACTIVE);
//        Tạo Payment tổng
        Payment payment = paymentService.save(
                Payment.builder()
                        .totalEnergy(BigDecimal.ZERO)
                        .totalCost(BigDecimal.ZERO)
                        .status(PaymentStatus.UNPAID)
                        .invoices(new ArrayList<>())
                        .paymentTransactions(new ArrayList<>())
                        .vehicleSubscription(vehicleSubscription)
                        .build());

        vehicleSubscriptionService.saveVehicle(vehicleSubscription);
        return "Success";
    }



//            PaymentTransaction paymentTransaction = paymentTransactionService.savePayment(
//                    PaymentTransaction.builder()
//                            .vehicleSubscription(vehicleSubscription)
//                            .amount(subscriptionPlan.getPrice())
//                            .paymentMethod("VNPAY")
//                            .vnpTxnRef(VNPayUtil.getRandomNumber(8))
//                            .status(PaymentTransactionEnum.FAILED)
//                            .paidAt(null)
//                            .bankCode("VNBANK")
//                            .build());
    
//
//    public PaymentTransactionResponse createBookingPayment(Long id) {
//    }


//    public PaymentTransactionResponse createPayment(Long id) {
//    }

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
