package com.group3.evproject.mapper;


import com.group3.evproject.dto.response.ChargingSessionResponseForStaff;
import com.group3.evproject.entity.ChargingSession;


public class ChargingSessionMapperForStaff {
    public static ChargingSessionResponseForStaff chargingSessionResponseForStaff(ChargingSession session) {
        if (session == null) return null;

        return ChargingSessionResponseForStaff.builder()
                .sessionId(session.getId())
                .stationName(session.getStation() != null ? session.getStation().getName() : null)
                .spotName(session.getSpot() != null ? session.getSpot().getSpotName() : null)
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
                .status(session.getStatus() != null ? session.getStatus().name() : null)
                .build();
    }
}
