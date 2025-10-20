package com.group3.evproject.controller;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.service.ChargingStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chargingStation")
@RequiredArgsConstructor
public class ChargingStationController {
    private final ChargingStationService chargingStationService;

    @GetMapping
    public ResponseEntity<List<ChargingStation>> getAllChargingStations() {
        List<ChargingStation> stations = chargingStationService.getAllChargingStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingStation> getChargingStationById(@PathVariable Long id) {
        ChargingStation station = chargingStationService.getChargingStationById(id);
        return ResponseEntity.ok(station);
    }

    @PostMapping
    public ResponseEntity<ChargingStation> createChargingStation(ChargingStation chargingStation) {
        ChargingStation createdStation = chargingStationService.createChargingStation(chargingStation);
        return ResponseEntity.ok(createdStation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargingStation> updateChargingStation(@PathVariable Long id, @RequestBody ChargingStation updatedStation) {
        ChargingStation station = chargingStationService.updateChargingStation(id, updatedStation);
        return ResponseEntity.ok(station);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargingStation(@PathVariable Long id) {
        chargingStationService.deleteChargingStation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ChargingStation>> searchChargingStations(@RequestParam String location){
        List<ChargingStation> stations = chargingStationService.findStationsByLocation(location);
        return ResponseEntity.ok(stations);
    }
}
