package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.PaymentTransactionResponse;
import com.group3.evproject.entity.PaymentTransaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentTransactionMapper{
    PaymentTransactionResponse toResponse(PaymentTransaction transaction);
}