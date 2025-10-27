package com.group3.evproject.dto.response;

import com.group3.evproject.entity.PaymentTransactionEnum;
import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransactionResponse {
    Long id;
    BigDecimal amount;
    String paymentMethod;
    PaymentTransactionEnum status;
    LocalDateTime createdAt;
    LocalDateTime paidAt;
}
