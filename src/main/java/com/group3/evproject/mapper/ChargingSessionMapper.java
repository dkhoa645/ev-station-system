package com.group3.evproject.mapper;

import com.group3.evproject.dto.response.ChargingSessionResponse;
import com.group3.evproject.dto.response.ChargingSessionSimpleResponse;
import com.group3.evproject.entity.ChargingSession;

public class ChargingSessionMapper {

    public static ChargingSessionSimpleResponse toSimpleResponse(ChargingSession session) {
        if (session == null) return null;

        return ChargingSessionSimpleResponse.builder()
                .sessionId(session.getId())
                .stationName(session.getStation() != null ? session.getStation().getName() : null)
                .spotName(session.getSpot() != null ? session.getSpot().getSpotName() : null)
                .bookingId(session.getBooking() != null ? session.getBooking().getId() : null)
                .startTime(session.getStartTime())
                .status(session.getStatus() != null ? session.getStatus().name() : "UNKNOWN")
                .build();
    }

    public static ChargingSessionResponse toDetailResponse(ChargingSession session) {
        if (session == null) return null;

        return ChargingSessionResponse.builder()
                .sessionId(session.getId())
                .stationName(session.getStation() != null ? session.getStation().getName() : null)
                .spotName(session.getSpot() != null ? session.getSpot().getSpotName() : null)
                .bookingId(session.getBooking() != null ? session.getBooking().getId() : null)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .chargingDuration(session.getChargingDuration())
                .powerOutput(session.getPowerOutput())
                .batteryCapacity(session.getBatteryCapacity())
                .percentBefore(session.getPercentBefore())
                .percentAfter(session.getPercentAfter())
                .energyUsed(session.getEnergyUsed())
                .ratePerKWh(session.getRatePerKWh())
                .totalCost(session.getTotalCost())
                .status(session.getStatus() != null ? session.getStatus().name() : "UNKNOWN")
                .build();
    }
}
