package com.group3.evproject.vnpay;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.group3.evproject.dto.response.ApiResponse;

import lombok.AccessLevel;

@RestController
@RequestMapping("payment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class PaymentController {
    VNPayService paymentService;
    @GetMapping("/vn-pay")
    public ApiResponse<VNPayDTO> pay(
            @RequestParam Long amount,
            @RequestParam(required = false) String bankCode,
            HttpServletRequest request) {
        return ApiResponse.<VNPayDTO>builder()
        .result(paymentService.createVnPayPayment(amount,bankCode,request))
        .build();
        // return ApiResponse<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }
    @GetMapping("/vn-pay-callback")
    public ApiResponse<String> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return ApiResponse.<String>builder()
            .result("Success")
            .build();
            // return new ApiResponse<>(HttpStatus.OK, "Success", new VNPayDTO("00", "Success", ""));
        } else {
            return ApiResponse.<String>builder()
            .result("Failed")
            .build();
            // return ApiResponse<>(HttpStatus.BAD_REQUEST, "Failed", null);
        }
    }
}
