package com.group3.evproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverInvoiceSummaryResponse {
    String driverName;
    Long invoiceCount;
    BigDecimal totalCost;
}
