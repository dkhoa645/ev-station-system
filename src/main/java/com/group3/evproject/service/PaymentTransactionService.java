package com.group3.evproject.service;

import com.group3.evproject.Enum.PaymentStatus;
import com.group3.evproject.Enum.PaymentTransactionStatus;
import com.group3.evproject.dto.response.BookingResponse;
import com.group3.evproject.entity.PaymentTransaction;
import com.group3.evproject.Enum.VehicleSubscriptionStatus;
import com.group3.evproject.dto.response.PaymentTransactionResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.exception.AppException;
import com.group3.evproject.exception.ErrorCode;
import com.group3.evproject.mapper.PaymentTransactionMapper;
import com.group3.evproject.mapper.VehicleSubscriptionMapper;
import com.group3.evproject.repository.PaymentTransactionRepository;
import com.group3.evproject.utils.UserUtils;
import com.group3.evproject.utils.VNPayUtil;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentTransactionService {
    PaymentTransactionRepository paymentTransactionRepository;
    PaymentTransactionMapper paymentTransactionMapper;
    VehicleSubscriptionService vehicleSubscriptionService;
    VehicleSubscriptionMapper vehicleSubscriptionMapper;
    PaymentService paymentService;
    BookingService bookingService;
    UserUtils userUtils;

    SubscriptionPlanService subscriptionPlanService;
    AuthenticationService authenticationService;
    UserService userService;


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

    public PaymentTransaction createPaymentTransaction(
            VehicleSubscription vehicleSubscription,
            Booking booking,
            Payment payment,
            BigDecimal amount,
            User user){
        return PaymentTransaction.builder()
                .vehicleSubscription(vehicleSubscription)
                .booking(booking)
                .payment(payment)
                .amount(amount)
                .paymentMethod("VNPAY")
                .vnpTxnRef(VNPayUtil.getRandomNumber(8))
                .status(PaymentTransactionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .paidAt(null)
                .bankCode("VNBANK")
                .user(user)
                .build();
    }

    @Transactional
    public PaymentTransactionResponse createSubscriptionPayment(Long id) {
        VehicleSubscription vehicleSubscription = vehicleSubscriptionService.findById(id);
        if (!vehicleSubscription.getStatus().equals(VehicleSubscriptionStatus.PENDING)) {
                throw  new AppException(ErrorCode.PENDING_STATUS);
        }
        SubscriptionPlan subscriptionPlan = vehicleSubscription.getSubscriptionPlan();
        BigDecimal amount = subscriptionPlan.getPrice();
        User user = userUtils.getCurrentUser();
        PaymentTransaction paymentTransaction =
                savePayment(createPaymentTransaction(vehicleSubscription,null,null,amount,user));

        vehicleSubscription.getPaymentTransactions().add(paymentTransaction);
        vehicleSubscriptionService.saveVehicleSubscription(vehicleSubscription);

        PaymentTransactionResponse response = paymentTransactionMapper.toResponse(paymentTransaction);
        response.setId(paymentTransaction.getId());
        response.setType("VEHICLE SUBSCRIPTION");
        return response;
    }


    public PaymentTransactionResponse createBookingPayment(Long id) {
            Booking booking = bookingService.findBookingById(id);
        if (!booking.getStatus().equals(Booking.BookingStatus.PENDING)) {
            throw new AppException(ErrorCode.PENDING_STATUS,"PENDING");
        }
        User user = userUtils.getCurrentUser();
        PaymentTransaction paymentTransaction =
                savePayment(createPaymentTransaction(null,booking,null,booking.getReservationFee(),user));
        BookingResponse bookingResponse = BookingResponse.builder()
                .bookingId(booking.getId())
                .vehicleId(booking.getVehicle().getId())
                .stationName(booking.getStation().getName())
                .startTime(booking.getStartTime())
                .timeToCharge(booking.getTimeToCharge())
                .reservationFee(booking.getReservationFee())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .build();
        booking.getPaymentTransactions().add(paymentTransaction);
        bookingService.saveBooking(booking);
        PaymentTransactionResponse response = paymentTransactionMapper.toResponse(paymentTransaction);
        response.setType("BOOKING");
        return  response;
    }


    public PaymentTransactionResponse createForPayment(Long id) {
        User user = userUtils.getCurrentUser();
        Payment payment = paymentService.findById(id);
        if (!payment.getStatus().equals(PaymentStatus.UNPAID)) {
            throw new AppException(ErrorCode.PENDING_STATUS,"UNPAID");
        }

        BigDecimal amount = payment.getTotalCost().subtract(payment.getPaidCost());

        PaymentTransaction paymentTransaction =
                savePayment(createPaymentTransaction(null,null,payment,amount,user));

        payment.getPaymentTransactions().add(paymentTransaction);
        paymentService.save(payment);

        PaymentTransactionResponse response = paymentTransactionMapper.toResponse(paymentTransaction);
        response.setId(paymentTransaction.getId());
        response.setType("PAYMENT");
        return response;
    }

    @Transactional
    public String processSuccessfulPayment(String ref) {
//        Tìm transaction có mã giao dịch ref
        PaymentTransaction paymentTransaction = paymentTransactionRepository.findByVnpTxnRef(ref)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"VnpTxnRef"));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        paymentTransaction.setPaidAt(now);
        paymentTransaction.setStatus(PaymentTransactionStatus.SUCCESS);
        //  Kiểm tra xem hóa đơn của object nào
        VehicleSubscription checkSubscription = paymentTransaction.getVehicleSubscription();
        Payment checkPayment = paymentTransaction.getPayment();
        Booking checkBooking = paymentTransaction.getBooking();
//                  Cập nhật Subscription
        if (checkSubscription != null) {
            checkSubscription.setStartDate(now);
            checkSubscription.setEndDate(now.plusMonths(1));
            checkSubscription.setStatus(VehicleSubscriptionStatus.ACTIVE);
            vehicleSubscriptionService.saveVehicleSubscription(checkSubscription);
            return "subscriptionSuccess";
        } else if (checkBooking != null) {
            checkBooking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingService.saveBooking(checkBooking);
            return "bookingSuccess";
        } else if (checkPayment != null) {
            checkPayment.setPaidCost(checkPayment.getPaidCost().add(paymentTransaction.getAmount()));
            checkPayment.setStatus(PaymentStatus.PAID);
            paymentService.save(checkPayment);
            if (checkPayment.getCompany() != null) {
                return "companyPaymentSuccess";
            }else{
            return "userPaymentSuccess";}
        }
        return "fail";
    }

    public List<PaymentTransactionResponse> getByUser() {
            User user = userUtils.getCurrentUser();
            return paymentTransactionRepository.findByUser(user).stream()
                    .map(entity -> {
                        PaymentTransactionResponse response = paymentTransactionMapper.toResponse(entity);
                        response.setId(entity.getId());
                        String type ="";
                        if(entity.getBooking() != null) type ="BOOKING";
                        if(entity.getPayment() != null) type ="PAYMENT";
                        if(entity.getVehicleSubscription() != null) type ="VEHICLE SUBSCRIPTION";
                        response.setType(type);
                        return response;
                    })
                    .collect(Collectors.toList());
    }

    public List<PaymentTransactionResponse> getAllTransaction() {
        return paymentTransactionRepository.findAll().stream()
                .map(entity -> {
                    PaymentTransactionResponse response = paymentTransactionMapper.toResponse(entity);
                    response.setId(entity.getId());
                    String type ="";
                    if(entity.getBooking() != null) type ="BOOKING";
                    if(entity.getPayment() != null) type ="PAYMENT";
                    if(entity.getVehicleSubscription() != null) type ="VEHICLE SUBSCRIPTION";
                    response.setType(type);
                    return response;
                })
                .collect(Collectors.toList());
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


//@Transactional
//public String processSuccessfulPayment(String vnpTxnRef) {
//
//    //  Tìm payment bằng vnpTxnRef
//    PaymentTransaction paymentTransaction = paymentTransactionRepository
//            .findByVnpTxnRef(vnpTxnRef)
//            .orElseThrow(() -> new AppException(
//                    ErrorCode.RESOURCES_NOT_EXISTS,
//                    "Payment transaction with ref: " + vnpTxnRef
//            ));
//
//    //  Kiểm tra trạng thái để tránh xử lý trùng
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
//    // Cập nhật payment
//    paymentTransaction.setStatus(PaymentStatusEnum.SUCCESS);
//    paymentTransaction.setPaidAt(now);
//
//    //  Cập nhật hoặc kích hoạt subscription
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
//    //  Xử lý usage
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
//    //  Save parent entity (cascade sẽ save các child)
//    vehicleSubscriptionService.saveVehicle(vehicleSubscription);
//    paymentTransactionRepository.save(paymentTransaction);
//
//    return "Success";
//}
}
