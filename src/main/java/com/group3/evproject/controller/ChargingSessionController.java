package com.group3.evproject.controller;

import com.group3.evproject.dto.request.EndRequest;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.mapper.ChargingSessionMapper;
import com.group3.evproject.service.ChargingSessionService;
import com.group3.evproject.dto.response.ChargingSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.group3.evproject.dto.request.StartRequest;

import java.util.List;

@RestController
@RequestMapping("/api/charging-sessions")
@RequiredArgsConstructor
public class ChargingSessionController {

    private final ChargingSessionService chargingSessionService;

    @GetMapping
    public ResponseEntity<List<ChargingSession>> getAllSessions() {
        return ResponseEntity.ok(chargingSessionService.getAllSessions());
    }

    @GetMapping("/{session_id}")
    public ResponseEntity<?> getSessionById(@PathVariable("session_id") Long sessionId) {
        try {
            ChargingSessionResponse response = chargingSessionService.getSessionById(sessionId);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<?> getSessionsByVehicle(@PathVariable Long vehicleId) {
        List<ChargingSessionResponse> history = chargingSessionService.getSessionsByVehicle(vehicleId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/start/{booking_id}")
    public ResponseEntity<?> startSession(
            @PathVariable("booking_id") Long bookingId,
            @RequestBody StartRequest startRequest) {
        try {
            Long spotId = startRequest.getSpotId();
            Double percentBefore = startRequest.getPercentBefore();
            if (spotId == null || percentBefore == null) {
                return ResponseEntity.badRequest().body("Missing required field: spotId, percentBefore");
            }

            ChargingSession session = chargingSessionService.startSession(bookingId, spotId);
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
            @RequestBody EndRequest endRequest) {
        try {
            Double ratePerKWh = endRequest.getRatePerKWh();
            Double batteryCapacity = endRequest.getBatteryCapacity();
            Double percentBefore = endRequest.getPercentBefore();

            if (ratePerKWh == null || batteryCapacity == null) {
                return ResponseEntity.badRequest().body("Missing required fields: ratePerKWh");
            }

            ChargingSession session = chargingSessionService.endSession(batteryCapacity ,ratePerKWh, sessionId, percentBefore);

            //Dùng Mapper để trả về DTO đầy đủ
            return ResponseEntity.ok(ChargingSessionMapper.toDetailResponse(session));

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body("Error ending session: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }


    @PostMapping("/cancel/{session_id}")
    public ResponseEntity<?> cancelSession(@PathVariable("session_id") Long sessionId) {
        try {
            ChargingSession session = chargingSessionService.cancelSession(sessionId);
            return ResponseEntity.ok(session);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error cancelling session: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSession(@PathVariable Long id) {
            chargingSessionService.deleteSession(id);
            return ResponseEntity.ok("Charging session with ID " + id + " deleted successfully.");
    }
}
