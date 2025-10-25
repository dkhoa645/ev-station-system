package com.group3.evproject.controller;


import com.group3.evproject.service.PaymentTransactionService;
import com.group3.evproject.vnpay.VNPayDTO;
import com.group3.evproject.vnpay.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group3.evproject.dto.response.ApiResponse;

import lombok.AccessLevel;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class PaymentController {
    VNPayService paymentService;
    PaymentTransactionService paymentTransactionService;
    @GetMapping("/vn-pay")
    public ApiResponse<VNPayDTO> pay(
            Long paymentTransactionId,
            HttpServletRequest request) {
        return ApiResponse.<VNPayDTO>builder()
        .result(paymentService.createVnPayPayment(paymentTransactionId,request))
        .build();
    }
    @GetMapping("/vn-pay-callback")
    public ApiResponse<String>  payCallbackHandler(HttpServletResponse response, HttpServletRequest request) {
        try{
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
            } } catch (Exception e) {
            return ApiResponse.<String>builder().result("Error").build();
        }
    }
}
