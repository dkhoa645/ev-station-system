package com.group3.evproject.controller;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.service.ChargingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/charging-sessions")
@RequiredArgsConstructor
public class ChargingSessionController {

    private final ChargingSessionService chargingSessionService;

    //ắt đầu phiên sạc (nhập bookingId + chargingPointId)
    @PostMapping("/start")
    public ResponseEntity<ChargingSession> startSession(
            @RequestParam Long bookingId,
            @RequestParam Long chargingPointId) {

        ChargingSession session = chargingSessionService.startSession(bookingId, chargingPointId);
        return ResponseEntity.ok(session);
    }

    //Kết thúc phiên sạc
    @PostMapping("/end/{sessionId}")
    public ResponseEntity<ChargingSession> endSession(
            @PathVariable Long sessionId,
            @RequestParam double batteryEnd,
            @RequestParam double energyUsed) {

        ChargingSession session = chargingSessionService.endSession(sessionId, batteryEnd, energyUsed);
        return ResponseEntity.ok(session);
    }

    //Hủy phiên sạc
    @PostMapping("/cancel/{sessionId}")
    public ResponseEntity<ChargingSession> cancelSession(@PathVariable Long sessionId) {
        ChargingSession session = chargingSessionService.cancelSession(sessionId);
        return ResponseEntity.ok(session);
    }

    //Lấy thông tin session theo ID
    @GetMapping("/{sessionId}")
    public ResponseEntity<ChargingSession> getSessionById(@PathVariable Long sessionId) {
        ChargingSession session = chargingSessionService.getSessionById(sessionId);
        return ResponseEntity.ok(session);
    }
}
