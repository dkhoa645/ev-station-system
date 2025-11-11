package com.group3.evproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationRevenueResponse {
    private Long stationId;
    private Double totalSession;
    private Double totalBooking;
}
