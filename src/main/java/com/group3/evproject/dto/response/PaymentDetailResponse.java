package com.group3.evproject.dto.response;

import com.group3.evproject.Enum.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentDetailResponse {
    Long id;
    BigDecimal totalEnergy;
    BigDecimal totalCost;
    PaymentStatus paymentStatus;
    LocalDateTime paidAt;
    LocalDateTime period;
}
