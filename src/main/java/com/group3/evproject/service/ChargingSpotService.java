package com.group3.evproject.service;
import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.repository.ChargingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingSpotService {
    private final ChargingSpotRepository chargingSpotRepository;

    public List<ChargingSpot> getAllSpots(){
        return chargingSpotRepository.findAll();
    }

    public ChargingSpot getSpotById(Integer id) {
        return chargingSpotRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging Spot does not found with id" + id));
    }

    public List<ChargingSpot> getSpotsByStationId(Integer stationId) {
        return chargingSpotRepository.findByStationId(stationId);
    }

    public List<ChargingSpot> getSpotsByStatus(String status) {
        return chargingSpotRepository.findByStatusIgnoreCase(status);
    }

    public ChargingSpot createSpot(ChargingSpot spot) {
        return chargingSpotRepository.save(spot);
    }

    public ChargingSpot updateSpot(ChargingSpot updatedSpot, Integer id) {
        ChargingSpot existing = chargingSpotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging Spot does not found with id" + id));
        existing.setName(updatedSpot.getName());
        existing.setPowerOutput(updatedSpot.getPowerOutput());
        existing.setStatus(updatedSpot.getStatus());
        existing.setStation(updatedSpot.getStation());
        return chargingSpotRepository.save(existing);
    }

    public void deleteSpot(Integer id) {
        if(!chargingSpotRepository.existsById(id)) {
            throw new RuntimeException("Charging Spot does not found with id" + id);
        }
        chargingSpotRepository.deleteById(id);
    }
}
