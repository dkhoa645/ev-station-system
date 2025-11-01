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

    public List<ChargingSpot> getSpotsByStatus(ChargingSpot.SpotStatus status) {
        return chargingSpotRepository.findByStatus(status);
    }


    public ChargingSpot createSpot(Long stationId, ChargingSpot.SpotType spotType) {
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging Station not found with id: " + stationId));

        int spotCount = chargingSpotRepository.findByStationId(stationId).size() + 1;
        String spotName = station.getName().replaceAll("\\s+", "") + "-SP" + String.format("%02d", spotCount);

        ChargingSpot newSpot = ChargingSpot.builder()
                .spotName(spotName)
                .powerOutput(station.getPowerCapacity())
                .status(ChargingSpot.SpotStatus.AVAILABLE)
                .spotType(spotType)
                .station(station)
                .available(true)
                .build();

        //lưu spot mới
        ChargingSpot saveSpot = chargingSpotRepository.save(newSpot);

        //tăng totalSpot +1 cho station
        station.setTotalSpots(station.getTotalSpots() + 1);

        //update lại availableSpots
        int available = getAvailableSpots(station);
        station.setAvailableSpots(available);

        chargingStationRepository.save(station);

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
        ChargingSpot spot = chargingSpotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging Spot not found with id: " + id));

        ChargingStation station = spot.getStation();
        if (station.getTotalSpots() > 0) {
            station.setTotalSpots(station.getTotalSpots() - 1);
            chargingStationRepository.save(station);
        }

        chargingSpotRepository.delete(spot);
    }

    public long countOccupiedSpots (Long stationId){
        return chargingSpotRepository.countByStationIdAndStatus(stationId, ChargingSpot.SpotStatus.OCCUPIED);
    }

    public int getAvailableSpots(ChargingStation station){
        long occupied = countOccupiedSpots(station.getId());
        int available = station.getTotalSpots() - (int) occupied;
        return Math.max(available,0); // tránh âm
    }

    public ChargingSpot getAvailableSpot(ChargingStation station){
        return chargingSpotRepository.findFirstByStationAndStatus(station, ChargingSpot.SpotStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("No available Spot found for this station"));
    }
}
