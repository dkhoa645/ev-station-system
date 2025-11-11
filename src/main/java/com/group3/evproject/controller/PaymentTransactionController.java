package com.group3.evproject.controller;


import com.group3.evproject.dto.response.PaymentTransactionResponse;
import com.group3.evproject.entity.User;
import com.group3.evproject.service.BookingService;
import com.group3.evproject.service.PaymentTransactionService;
import com.group3.evproject.utils.UserUtils;
import com.group3.evproject.vnpay.VNPayDTO;
import com.group3.evproject.vnpay.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.*;

import com.group3.evproject.dto.response.ApiResponse;

import lombok.AccessLevel;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/payment-transaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class PaymentTransactionController {
    VNPayService paymentService;
    PaymentTransactionService paymentTransactionService;
    UserUtils userUtils;

    @PostMapping("/subscription/{id}")
    public ApiResponse<PaymentTransactionResponse> createSubscription(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResponse.<PaymentTransactionResponse>builder()
                .result(paymentTransactionService.createSubscriptionPayment(id))
                .build();
    }

    @PostMapping("/booking/{id}")
    public ApiResponse<PaymentTransactionResponse> createBooking(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResponse.<PaymentTransactionResponse>builder()
                .result(paymentTransactionService.createBookingPayment(id))
                .build();
    }

    @PostMapping("payment/{id}")
    public ApiResponse<PaymentTransactionResponse> createPayment(
            @PathVariable Long id
    ){
        return ApiResponse.<PaymentTransactionResponse>builder()
                .result(paymentTransactionService.createForPayment(id))
                .build();
    }

    @PostMapping("/vn-pay")
    public ApiResponse<VNPayDTO> pay(
            Long paymentTransactionId,
            HttpServletRequest request) {
        return ApiResponse.<VNPayDTO>builder()
                .result(paymentService.createVnPayPayment(paymentTransactionId, request))
                .build();
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletResponse response, HttpServletRequest request) throws IOException {
            String status = request.getParameter("vnp_ResponseCode");
            String ref = request.getParameter("vnp_TxnRef");
            User user = userUtils.getCurrentUser();
            if (status.equals("00")) {
                String result = paymentTransactionService.processSuccessfulPayment(ref);
                if (result.equals("bookingSuccess")) {
                    response.sendRedirect("http://localhost:5173/bookingPaymentSuccess");
                }else if(result.equals("subscriptionSuccess")) {
                    response.sendRedirect("http://localhost:5173/success");
                }else if(result.equals("paymentSuccess")) {
                    response.sendRedirect("http://localhost:5173/payment/invoice/" + user.getId());
                }
            } else {
                response.sendRedirect("http://localhost:5173/fail");
            }
    }

    @GetMapping
    public ApiResponse<List<PaymentTransactionResponse>> getPaymentTransaction(){
        return ApiResponse.<List<PaymentTransactionResponse>>builder()
                .result(paymentTransactionService.getByUser())
                .build();
    }


}
