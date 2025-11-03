package com.group3.evproject.controller;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.mapper.ChargingSessionMapper;
import com.group3.evproject.service.ChargingSessionService;
import com.group3.evproject.dto.response.ChargingSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/start/{booking_id}")
    public ResponseEntity<?> startSession(
            @PathVariable("booking_id") Long bookingId,
            @RequestBody Map<String, Long> body) {
        try {
            Long spotId = body.get("spotId");
            if (spotId == null) {
                return ResponseEntity.badRequest().body("Missing required field: spotId");
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
            @RequestBody Map<String, Double> body) {
        try {
            Double ratePerKWh = body.get("ratePerKWh");
            Double percentBefore = body.get("percentBefore");
            Double batteryCapacity = body.get("batteryCapacity");

            if (ratePerKWh == null || percentBefore == null || batteryCapacity == null) {
                return ResponseEntity.badRequest().body("Missing required fields: ratePerKWh, percentBefore, batteryCapacity");
            }

            ChargingSession session = chargingSessionService.endSession(sessionId, ratePerKWh, percentBefore, batteryCapacity);

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
}
