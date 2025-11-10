package com.group3.evproject.controller.admin;


import com.group3.evproject.dto.response.ApiResponse;
import com.group3.evproject.dto.response.PaymentTransactionResponse;
import com.group3.evproject.service.PaymentTransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/payment-transaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class AdminPaymentTransactionController {
    PaymentTransactionService paymentTransactionService;

    @GetMapping
    public ApiResponse<List<PaymentTransactionResponse>> getPaymentTransaction(){
        return ApiResponse.<List<PaymentTransactionResponse>>builder()
                .result(paymentTransactionService.getAllTransaction())
                .build();
    }


}
