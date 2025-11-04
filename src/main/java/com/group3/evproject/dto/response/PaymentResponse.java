package com.group3.evproject.dto.response;

import com.group3.evproject.Enum.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PaymentResponse {
    Long userId;
    Long companyId;
    LocalDateTime period;
    PaymentStatus paymentStatus;
}
