package com.group3.evproject.vnpay;


import com.group3.evproject.entity.PaymentTransaction;
import com.group3.evproject.service.PaymentTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import java.util.*;

import com.group3.evproject.vnpay.VNPayUtil;

import lombok.AccessLevel;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public class VNPayService {
    VNPayConfig vnPayConfig;
    PaymentTransactionService paymentTransactionService;
    public VNPayDTO createVnPayPayment(Long paymentTransactionId,HttpServletRequest request) {
        PaymentTransaction paymentTransaction = paymentTransactionService.findById(paymentTransactionId);
        long amount = paymentTransaction.getAmount().longValue() * 100L;
        String bankCode = paymentTransaction.getBankCode();
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        vnpParamsMap.put("vnp_TxnRef",  paymentTransaction.getVnpTxnRef());
        vnpParamsMap.put("vnp_ReturnUrl", "http://localhost:5173/success");
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return VNPayDTO.builder()
        .code("ok")
        .message("success")
        .paymentUrl(paymentUrl)
        .build();
    }
}
