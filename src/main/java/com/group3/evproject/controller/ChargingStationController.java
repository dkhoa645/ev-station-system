package com.group3.evproject.controller;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.service.ChargingStationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chargingStation")
@RequiredArgsConstructor
public class ChargingStationController {
    private final ChargingStationService chargingStationService;

    @GetMapping
    public ResponseEntity<List<ChargingStation>> getAllChargingStations() {
        return ResponseEntity.ok(chargingStationService.getAllChargingStations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargingStation> getChargingStationById(@PathVariable Long id) {
        return ResponseEntity.ok(chargingStationService.getChargingStationById(id));
    }

    @PostMapping
    public ResponseEntity<ChargingStation> createChargingStation(@RequestBody ChargingStation chargingStation) {
        ChargingStation createdStation = chargingStationService.createChargingStation(chargingStation);
        return ResponseEntity.ok(createdStation);
    }

    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        ChargingStation station = chargingStationService.getChargingStationById(id);

        // Lưu file vào thư mục uploads/
        String uploadDir = "uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        // Cập nhật đường dẫn ảnh
        station.setImageUrl("/uploads/" + fileName);
        chargingStationService.createChargingStation(station);

        return ResponseEntity.ok("Uploaded successfully: " + station.getImageUrl());
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
