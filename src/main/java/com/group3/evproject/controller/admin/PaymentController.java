package com.group3.evproject.controller.admin;

import com.group3.evproject.dto.request.PaymentCreationRequest;
import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.PaymentDetailResponse;
import com.group3.evproject.dto.response.PaymentResponse;
import com.group3.evproject.entity.Payment;
import com.group3.evproject.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/payment")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {
    PaymentService paymentService;

    @GetMapping
    public ApiResponse<List<PaymentDetailResponse>> getPayment(){
        return ApiResponse.<List<PaymentDetailResponse>>builder()
                .result(paymentService.getAll())
                .build();
    }

    @GetMapping("/company/{id}")
    public ApiResponse<List<PaymentDetailResponse>> getPaymentByCompany(@PathVariable Long id){
        return ApiResponse.<List<PaymentDetailResponse>>builder()
                .result(paymentService.getCompany(id))
                .build();
    }

    @GetMapping("/user/{id}")
    public ApiResponse<List<PaymentDetailResponse>> getPaymentByUser(@PathVariable Long id){
        return ApiResponse.<List<PaymentDetailResponse>>builder()
                .result(paymentService.getUser(id))
                .build();
    }

    @PostMapping
    public ApiResponse<PaymentResponse> createPayment(@RequestBody PaymentCreationRequest request) {
        return ApiResponse.<PaymentResponse>builder()
                .result(paymentService.createPayment(request))
                .build();
    }

}
