package com.group3.evproject.dto.response;

import com.group3.evproject.entity.ChargingSession;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {

    Long id;
    LocalDateTime issueDate;
    BigDecimal finalCost;
    String status;
    ChargingSessionSimpleResponse session;
}
