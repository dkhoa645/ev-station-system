package com.group3.evproject.service;

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
import com.group3.evproject.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    SubscriptionPlanService subscriptionPlanService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

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
    public PaymentTransactionResponse createSubscriptionPayment(Long id, HttpServletRequest request) {
        VehicleSubscription vehicleSubscription = vehicleSubscriptionService.findById(id);
        if (!vehicleSubscription.getStatus().equals(VehicleSubscriptionStatus.PENDING)) {
                throw  new AppException(ErrorCode.PENDING_STATUS);
        }
        SubscriptionPlan subscriptionPlan = vehicleSubscription.getSubscriptionPlan();
        BigDecimal amount = subscriptionPlan.getPrice();
        String username= authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
        PaymentTransaction paymentTransaction =
                savePayment(createPaymentTransaction(vehicleSubscription,null,null,amount,user));

        vehicleSubscription.getPaymentTransactions().add(paymentTransaction);
        vehicleSubscriptionService.saveVehicle(vehicleSubscription);

        PaymentTransactionResponse response = paymentTransactionMapper.toResponse(paymentTransaction);
        response.setType("VEHICLE_SUBSCRIPTION");
        return response;
    }


    public PaymentTransactionResponse createBookingPayment(Long id, HttpServletRequest request) {
            Booking booking = bookingService.findBookingById(id);
        if (!booking.getStatus().equals(Booking.BookingStatus.PENDING)) {
            throw new AppException(ErrorCode.PENDING_STATUS);
        }
        String username= authenticationService.extractUsernameFromRequest(request);
        User user = userService.getUserByUsername(username);
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




    @Transactional
    public String processSuccessfulPayment(String ref) {
//        Tìm transaction có mã giao dịch ref
        com.group3.evproject.entity.PaymentTransaction paymentTransaction = paymentTransactionRepository.findByVnpTxnRef(ref)
                .orElseThrow(()->new AppException(ErrorCode.RESOURCES_NOT_EXISTS,"VnpTxnRef"));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        paymentTransaction.setPaidAt(now);
        paymentTransaction.setStatus(PaymentTransactionStatus.SUCCESS);
        //  Kiểm tra xem hóa đơn của object nào
        VehicleSubscription checkVehicleSubscription = paymentTransaction.getVehicleSubscription();
        Payment checkPayment = paymentTransaction.getPayment();
        Booking checkBooking = paymentTransaction.getBooking();
//                  Cập nhật Subscription
        if (checkVehicleSubscription != null) {
            checkVehicleSubscription.setStartDate(now);
            checkVehicleSubscription.setEndDate(now.plusMonths(1));
            checkVehicleSubscription.setStatus(VehicleSubscriptionStatus.ACTIVE);
//                      Tạo Payment tổng
            vehicleSubscriptionService.saveVehicle(checkVehicleSubscription);
        } else if (checkBooking != null) {
            checkBooking.setStatus(Booking.BookingStatus.CONFIRMED);
            bookingService.saveBooking(checkBooking);
            return "chargingSession";
        }


        return "Success";
    }

    public List<PaymentTransactionResponse> getAll() {
            return paymentTransactionRepository.findAll().stream()
                    .map(entity -> {
                        PaymentTransactionResponse response = paymentTransactionMapper.toResponse(entity);
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
