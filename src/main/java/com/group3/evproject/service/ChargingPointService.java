package com.group3.evproject.service;

import com.group3.evproject.entity.ChargingPoint;
import com.group3.evproject.repository.ChargingPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingPointService {

    private final ChargingPointRepository chargingPointRepository;

    public List<ChargingPoint> getAll() {
        return chargingPointRepository.findAll();
    }

    public ChargingPoint getById(Long id) {
        return chargingPointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));
    }

    public ChargingPoint create(ChargingPoint point) {
        return chargingPointRepository.save(point);
    }

    public ChargingPoint update(Long id, ChargingPoint point) {
        ChargingPoint existing = getById(id);
        existing.setStationId(point.getStationId());
        existing.setType(point.getType());
        existing.setStatus(point.getStatus());
        return chargingPointRepository.save(existing);
    }

    public void delete(Long id) {
        chargingPointRepository.deleteById(id);
    }
}
