package com.group3.evproject.controller;

import com.group3.evproject.entity.ChargingPoint;
import com.group3.evproject.service.ChargingPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charging-point")
@RequiredArgsConstructor
public class ChargingPointController {

    private final ChargingPointService chargingPointService;

    @GetMapping
    public ResponseEntity<List<ChargingPoint>> getAllChargingPoint() {
        return ResponseEntity.ok(chargingPointService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingPoint> getChargingPointById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingPointService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ChargingPoint> createChargingPoint(@RequestBody ChargingPoint point) {
        return ResponseEntity.ok(chargingPointService.create(point));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingPoint> updateChargingPoint(@PathVariable Long id, @RequestBody ChargingPoint point) {
        return ResponseEntity.ok(chargingPointService.update(id, point));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargingPoint(@PathVariable Long id) {
        chargingPointService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
