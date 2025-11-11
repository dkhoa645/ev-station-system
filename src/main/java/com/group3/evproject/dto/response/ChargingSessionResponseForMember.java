package com.group3.evproject.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChargingSessionResponseForMember {
    private Long sessionId;
    private String stationName;
    private Long stationId;
    private Long vehicleId;
    private String spotName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double chargingDuration;
    private Double powerOutput;
    private Double batteryCapacity;
    private Double percentBefore;
    private Double percentAfter;
    private Double energyUsed;
    private Double ratePerKWh;
    private Double totalCost;
    private String status;
}
