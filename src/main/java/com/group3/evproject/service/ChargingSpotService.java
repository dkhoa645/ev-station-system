package com.group3.evproject.service;
import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.repository.ChargingSpotRepository;
import com.group3.evproject.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChargingSpotService {
    private final ChargingSpotRepository chargingSpotRepository;
    private final ChargingStationRepository chargingStationRepository;

    public List<ChargingSpot> getAllSpots(){
        return chargingSpotRepository.findAll();
    }

    public ChargingSpot getSpotById(Long id) {
        return chargingSpotRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging Spot does not found with id" + id));
    }

    public List<ChargingSpot> getSpotsByStationId(Long stationId) {
        return chargingSpotRepository.findByStationId(stationId);
    }

    public List<ChargingSpot> getSpotsByStatus(ChargingSpot.SpotStatus status) {
        return chargingSpotRepository.findByStatus(status);
    }

    @Transactional
    public ChargingSpot createSpot(Long stationId, ChargingSpot.SpotType spotType) {
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging Station not found with id: " + stationId));

        String spotName = station.getName().replaceAll("\\s+", "") + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ChargingSpot newSpot = ChargingSpot.builder()
                .spotName(spotName)
                .powerOutput(station.getPowerCapacity())
                .status(ChargingSpot.SpotStatus.AVAILABLE)
                .spotType(spotType)
                .station(station)
                .build();
        return chargingSpotRepository.save(newSpot);
    }

    public ChargingSpot updateSpot(ChargingSpot updatedSpot, Long id) {
        ChargingSpot existing = chargingSpotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging Spot not found with id: " + id));

        if (updatedSpot.getStatus() != null) {
            existing.setStatus(updatedSpot.getStatus());
        }
        if (updatedSpot.getPowerOutput() != null) {
            existing.setPowerOutput(updatedSpot.getPowerOutput());
        }
        if (updatedSpot.getSpotType() != null) {
            existing.setSpotType(updatedSpot.getSpotType());
        }
        return chargingSpotRepository.save(existing);
    }

    public void deleteSpot(Long id) {
        if(!chargingSpotRepository.existsById(id)) {
            throw new RuntimeException("Charging Spot does not found with id" + id);
        }
        chargingSpotRepository.deleteById(id);
    }
}
