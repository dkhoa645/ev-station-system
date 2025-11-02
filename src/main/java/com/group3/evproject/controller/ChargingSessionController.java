package com.group3.evproject.controller;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.service.ChargingSessionService;
import com.group3.evproject.dto.response.ChargingSessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/start/{booking_id}")
    public ResponseEntity<?> startSession(@PathVariable("booking_id") Long bookingId) {
        try {
            ChargingSession session = chargingSessionService.startSession(bookingId);
            return ResponseEntity.ok(session);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error starting session: " + ex.getMessage());
        }
    }

    @PostMapping("/end/{session_id}")
    public ResponseEntity<?> endSession(
            @PathVariable("session_id") Long sessionId,
            @RequestParam Double ratePerKWh,
            @RequestParam Double percentBefore,
            @RequestParam Double batteryCapacity
    ) {
        try {
            ChargingSession session = chargingSessionService.endSession(sessionId, ratePerKWh, percentBefore, batteryCapacity);
            return ResponseEntity.ok(session);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error ending session: " + ex.getMessage());
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
