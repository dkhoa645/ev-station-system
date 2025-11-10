package com.group3.evproject.controller.driver;

import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.PaymentDetailResponse;
import com.group3.evproject.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/driver/payment")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverPaymentController {
    PaymentService paymentService;

    @GetMapping
    public ApiResponse<List<PaymentDetailResponse>> getPaymentByUser(@PathVariable Long id){
        return ApiResponse.<List<PaymentDetailResponse>>builder()
                .result(paymentService.getUser(id))
                .build();
    }


}
