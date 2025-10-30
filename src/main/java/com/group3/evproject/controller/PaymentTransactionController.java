package com.group3.evproject.controller;


import com.group3.evproject.dto.response.PaymentTransactionResponse;
import com.group3.evproject.service.BookingService;
import com.group3.evproject.service.PaymentTransactionService;
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
@RequestMapping("payment-transaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class PaymentTransactionController {
    VNPayService paymentService;
    PaymentTransactionService paymentTransactionService;
    BookingService bookingService;

    @PostMapping("/subscription/{id}")
    public ApiResponse<PaymentTransactionResponse> createSubscription(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResponse.<PaymentTransactionResponse>builder()
                .result(paymentTransactionService.createSubscriptionPayment(id, request))
                .build();
    }

    @PostMapping("/booking/{id}")
    public ApiResponse<PaymentTransactionResponse> createBooking(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResponse.<PaymentTransactionResponse>builder()
                .result(paymentTransactionService.createBookingPayment(id, request))
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
            if (status.equals("00")) {
                String result = paymentTransactionService.processSuccessfulPayment(ref);
                if (result.equals("Success")) {
                    response.sendRedirect("http://localhost:5173/success");
                }
            } else {
                response.sendRedirect("http://localhost:5173/fail");
            }
    }

//    @GetMapping
//    public ApiResponse<List<PaymentTransactionResponse>> getPaymentTransaction(){
//        return ApiResponse.<List<PaymentTransactionResponse>>builder()
//                .result(paymentTransactionService.getAll())
//                .build();
//    }

//    public void verifyEmail(@RequestParam String token, HttpServletResponse response) throws IOException {
//        String message = authenticationService.verifyEmail(token);
//        if (message.contains("success")) {
//            response.sendRedirect("http://localhost:5173/");
//        } else {
//            response.sendRedirect("http://localhost:5173/");
//        }
//    }
}
