package com.group3.evproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyPaymentDetailResponse {
    Long id;
    LocalDateTime period;
    BigDecimal totalCost;
    Long invoiceCount;
    Map<String,DriverInvoiceSummaryResponse> driverDetails;
}
