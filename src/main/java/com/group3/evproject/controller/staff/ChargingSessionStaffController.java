package com.group3.evproject.controller.staff;

import com.group3.evproject.dto.request.EndRequestForStaff;
import com.group3.evproject.dto.request.StartRequestForStaff;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.mapper.ChargingSessionMapper;
import com.group3.evproject.service.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff/sessions")
@RequiredArgsConstructor
@CrossOrigin("*")

public class ChargingSessionStaffController {
    private final ChargingSessionService chargingSessionService;

    @PostMapping("/start")
    public ResponseEntity<?> startSession(@RequestBody StartRequestForStaff startRequestForStaff) {
        try {
            Long spotId = startRequestForStaff.getSpotId();
            Long userId = startRequestForStaff.getUserId();
            Long vehicleId = startRequestForStaff.getVehicleId();
            Long stationId = startRequestForStaff.getStationId();
            Double percentBefore = startRequestForStaff.getPercentBefore();
            if (spotId == null || userId == null || vehicleId == null || stationId == null|| percentBefore == null) {
                return ResponseEntity.badRequest().body("Missing required field: spotId, percentBefore");
            }

            ChargingSession session = chargingSessionService.startSessionForStaff(spotId,userId,vehicleId,stationId,percentBefore);
            return ResponseEntity.ok(ChargingSessionMapper.toSimpleResponse(session));

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error starting session: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }

    @PutMapping("/end/{session_id}")
    public ResponseEntity<?> endSession(
            @PathVariable("session_id") Long sessionId,
            @RequestBody EndRequestForStaff endRequestForStaff) {
        try {
            Double ratePerKWh = endRequestForStaff.getRatePerKWh();
            Double batteryCapacity = endRequestForStaff.getBatteryCapacity();
            Double percentBefore = endRequestForStaff.getPercentBefore();

            if (ratePerKWh == null || batteryCapacity == null) {
                return ResponseEntity.badRequest().body("Missing required fields: ratePerKWh");
            }

            ChargingSession session = chargingSessionService.endSessionForStaff(batteryCapacity,ratePerKWh,sessionId,percentBefore);

            //Dùng Mapper để trả về DTO đầy đủ
            return ResponseEntity.ok(ChargingSessionMapper.toDetailResponse(session));

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error ending session: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }
}
