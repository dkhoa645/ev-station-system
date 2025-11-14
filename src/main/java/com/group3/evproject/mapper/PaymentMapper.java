package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.CompanyPaymentDetailResponse;
import com.group3.evproject.dto.response.PaymentDetailResponse;
import com.group3.evproject.dto.response.PaymentResponse;
import com.group3.evproject.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse(Payment payment);

    PaymentDetailResponse toPaymentDetailResponse(Payment payment);

    CompanyPaymentDetailResponse toCompanyPaymentDetailResponse(Payment payment);
}
