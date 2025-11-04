package com.group3.evproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChargingSessionResponse {

    Long sessionId;
    String stationName;
    String spotName;
    Long bookingId;

    LocalDateTime startTime;
    LocalDateTime endTime;
    Double chargingDuration;

    Double powerOutput;
    Double batteryCapacity;

    Double percentBefore;
    Double percentAfter;

    Double energyUsed;
    Double ratePerKWh;
    Double totalCost;

    String status;
}
