package com.group3.evproject.service;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChargingStationService {
    private final ChargingStationRepository chargingStationRepository;

    public List<ChargingStation> getAllChargingStations() {
        return chargingStationRepository.findAll();
    }

    public ChargingStation getChargingStationById(Long id) {
        ChargingStation station = chargingStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + id));
        if (station.getSpots() != null && !station.getSpots().isEmpty()) {
            station.getSpots().size();
        }
        return station;
    }

    public ChargingStation createChargingStation(ChargingStation chargingStation) {
        return chargingStationRepository.save(chargingStation);
    }

    public ChargingStation updateChargingStation(Long id, ChargingStation updatedStation) {
        Optional<ChargingStation> existing = chargingStationRepository.findById(id);
        if (existing.isPresent()) {
            ChargingStation station = existing.get();

            if (updatedStation.getName() != null) {
                station.setName(updatedStation.getName());
                return chargingStationRepository.save(station);
            }
            if (updatedStation.getLocation() != null) {
                station.setLocation(updatedStation.getLocation());
            }
            if (updatedStation.getStatus()!=null) {
                station.setStatus(updatedStation.getStatus());
            }
            if (updatedStation.getPowerCapacity() != null) {
                station.setPowerCapacity(updatedStation.getPowerCapacity());
            }
            if (updatedStation.getAvailableSpots() != null) {
                station.setAvailableSpots(updatedStation.getAvailableSpots());
            }
            return  chargingStationRepository.save(station);
        } else {
            throw new RuntimeException("Charging station not found with id: " + id);
        }
    }

    public void deleteChargingStation(Long id) {
        if (!chargingStationRepository.existsById(id)) {
            throw new RuntimeException("Charging station not found with id: " + id);
        }
        chargingStationRepository.deleteById(id);
    }

    public List<ChargingStation> findStationsByLocation(String location) {
        return chargingStationRepository.findByLocationContainingIgnoreCase(location);
    }
}
