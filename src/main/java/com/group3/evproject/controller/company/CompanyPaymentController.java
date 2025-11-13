package com.group3.evproject.controller.company;

import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.CompanyPaymentSummaryResponse;
import com.group3.evproject.dto.response.PaymentDetailResponse;
import com.group3.evproject.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/company/payment")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyPaymentController {

    PaymentService paymentService;

    @GetMapping
    public ApiResponse<List<PaymentDetailResponse>> getPayment(){
        return ApiResponse.<List<PaymentDetailResponse>>builder()
                .result(paymentService.getForCompany())
                .build();
    }

//    @GetMapping("/{userId}")
//    public ApiResponse<List<CompanyPaymentSummaryResponse>> getPaymentDetail(){
//            return ApiResponse.<List<CompanyPaymentSummaryResponse>>builder()
//                    .result(paymentService.getDetailForCompany())
//                    .build();
//    }

}
