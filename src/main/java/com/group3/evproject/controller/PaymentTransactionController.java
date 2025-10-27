package com.group3.evproject.controller;


import com.group3.evproject.dto.response.PaymentTransactionResponse;
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

@RestController
@RequestMapping("paymentTransaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class PaymentTransactionController {
    VNPayService paymentService;
    PaymentTransactionService paymentTransactionService;


    @PostMapping("/subscription/{id}")
    public ApiResponse<PaymentTransactionResponse> create(
            @PathVariable Long id,
            HttpServletRequest request) {
        return ApiResponse.<PaymentTransactionResponse>builder()
                .result(paymentTransactionService.createSubscriptionPayment(id, request))
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
    public ApiResponse<String> payCallbackHandler(HttpServletResponse response, HttpServletRequest request) {
            String status = request.getParameter("vnp_ResponseCode");
            String ref = request.getParameter("vnp_TxnRef");
            if (status.equals("00")) {
                String result = paymentTransactionService.processSuccessfulPayment(ref);
                return ApiResponse.<String>builder()
                        .result(result)
                        .build();
            } else {
                return ApiResponse.<String>builder()
                        .result("Failed")
                        .build();
            }
    }
}
