package com.group3.evproject.controller;
import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.service.ChargingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import  java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class ChargingSpotController {
    private final ChargingSpotService chargingSpotService;

    @GetMapping
    public ResponseEntity<List<ChargingSpot>> getAllSpots(){
        return ResponseEntity.ok(chargingSpotService.getAllSpots());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingSpot> getSpotById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingSpotService.getSpotById(id));
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<ChargingSpot>> getSpotsByStationId(@PathVariable Long stationId) {
        return ResponseEntity.ok(chargingSpotService.getSpotsByStationId(stationId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ChargingSpot>> getSpotsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(chargingSpotService.getSpotsByStatus(status));
    }

    @PostMapping
    public ResponseEntity<ChargingSpot> createSpot(@RequestBody ChargingSpot chargingSpot) {
        return ResponseEntity.ok(chargingSpotService.createSpot(chargingSpot));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingSpot> updateSpot(@RequestBody ChargingSpot updatedSpot, @PathVariable Long id) {
        return ResponseEntity.ok(chargingSpotService.updateSpot(updatedSpot, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        chargingSpotService.deleteSpot(id);
        return ResponseEntity.noContent().build();
    }
}
