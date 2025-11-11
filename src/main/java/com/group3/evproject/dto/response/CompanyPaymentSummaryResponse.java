package com.group3.evproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyPaymentSummaryResponse {
    String period;
    BigDecimal totalCost;
    Long invoiceCount;
    List<DriverInvoiceSummaryResponse> details;
}
