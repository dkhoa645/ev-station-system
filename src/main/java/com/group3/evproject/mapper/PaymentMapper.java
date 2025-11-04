package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.PaymentResponse;
import com.group3.evproject.entity.Payment;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse(Payment payment);
}
