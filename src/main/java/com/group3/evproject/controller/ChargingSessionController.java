package com.group3.evproject.controller;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.service.ChargingSessionService;
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
    public ResponseEntity<List<ChargingSession>> getChargingSessions() {
        return ResponseEntity.ok(chargingSessionService.getAllSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingSession> getChargingSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingSessionService.getSessionById(id));
    }

    @GetMapping("/by-booking/{bookingId}")
    public ResponseEntity<List<ChargingSession>> getSessionsByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(chargingSessionService.getSessionsByBooking(bookingId));
    }

    @GetMapping("/by-station/{stationId}")
    public ResponseEntity<List<ChargingSession>> getSessionsByStation(@PathVariable Long stationId) {
        return ResponseEntity.ok(chargingSessionService.getSessionsByStation(stationId));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<ChargingSession>> getSessionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(chargingSessionService.getSessionsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<ChargingSession> createChargingSession(@RequestBody ChargingSession chargingSession) {
        ChargingSession newSession = chargingSessionService.createSession(chargingSession);
        return ResponseEntity.ok(newSession);
    }

    @PostMapping("/start")
    public ResponseEntity<ChargingSession> startChargingSession(@RequestParam Long bookingId, @RequestParam Long stationId, @RequestParam double batteryStart, @RequestParam double totalBatteryCapacity, @RequestParam(required = false) double pricePerKwh) {
        return ResponseEntity.ok(
                chargingSessionService.startChargingSession(bookingId, stationId, totalBatteryCapacity, pricePerKwh)
        );
    }

    @PostMapping("/end")
    public ResponseEntity<ChargingSession> endChargingSession(@PathVariable Long id, @RequestParam double batteryEnd, @RequestParam double totalBatteryCapacity, @RequestParam double pricePerKwh) {
        return ResponseEntity.ok(
                chargingSessionService.endChargingSession(id, batteryEnd, totalBatteryCapacity, pricePerKwh)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingSession> updateChargingSession(@RequestBody ChargingSession session, @PathVariable Long id) {
        return ResponseEntity.ok(chargingSessionService.updateSession(session, id));
    }
}
