package com.group3.evproject.dto.response;

import com.group3.evproject.Enum.PaymentTransactionStatus;
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
    String vnpTxnRef;
    BigDecimal amount;
    String paymentMethod;
    PaymentTransactionStatus status;
    LocalDateTime createdAt;
    LocalDateTime paidAt;
    String type;
//    ehicleSubscription,booking,payment

}
