package com.group3.evproject.service;
import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.repository.ChargingSpotRepository;
import com.group3.evproject.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<ChargingSpot> getSpotsByStatus(String status) {
        return chargingSpotRepository.findByStatusIgnoreCase(status);
    }

    public ChargingSpot createSpot(Long stationId, ChargingSpot.SpotType spotType) {
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging Station not found with id: " + stationId));

        int spotCount = chargingSpotRepository.findByStationId(stationId).size() + 1;
        String spotName = station.getName().replaceAll("\\s+", "") + "-SP" + String.format("%02d", spotCount);

        ChargingSpot newSpot = ChargingSpot.builder()
                .spotName(spotName)
                .powerOutput(station.getPowerCapacity())
                .status("AVAILABLE")
                .available(true)
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
        return chargingSpotRepository.save(existing);
    }

    public void deleteSpot(Long id) {
        if(!chargingSpotRepository.existsById(id)) {
            throw new RuntimeException("Charging Spot does not found with id" + id);
        }
        chargingSpotRepository.deleteById(id);
    }
}
