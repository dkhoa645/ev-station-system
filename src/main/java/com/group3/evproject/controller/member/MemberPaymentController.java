package com.group3.evproject.controller.member;

import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.PaymentDetailResponse;
import com.group3.evproject.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/member/payment")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MemberPaymentController {
    PaymentService paymentService;

    @GetMapping
    public ApiResponse<List<PaymentDetailResponse>> getPaymentByUser(){
        return ApiResponse.<List<PaymentDetailResponse>>builder()
                .result(paymentService.getForUser())
                .build();
    }


}
