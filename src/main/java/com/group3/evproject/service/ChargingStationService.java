package com.group3.evproject.service;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingStationService {
    private final ChargingStationRepository chargingStationRepository;

    public List<ChargingStation> getAllChargingStations() {
        List<ChargingStation> stations = chargingStationRepository.findAll();
        stations.forEach(station -> {
            if (station.getSpots() != null) {
                station.getSpots().size();
            }
        });
        return stations;
    }

    public ChargingStation getChargingStationById(Long id) {
       ChargingStation station = chargingStationRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging station not found with id: " + id));
       if (station.getSpots() != null) {
           station.getSpots().size();
       }
       return station;
    }

    public ChargingStation createChargingStation(ChargingStation chargingStation) {
        if (chargingStation.getAvailableSpots() == null) {
            chargingStation.setAvailableSpots(0);
        }
        if (chargingStation.getBookingAvailabel() == null) {
            chargingStation.setBookingAvailabel(0);
        }
        return chargingStationRepository.save(chargingStation);
    }

    public ChargingStation updateChargingStation(Long id, ChargingStation updatedStation) {
        ChargingStation station = chargingStationRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging station not found with id: " + id));
        if (updatedStation.getName() != null) {
            station.setName(updatedStation.getName());
        }
        if (updatedStation.getLocation() != null) {
            station.setLocation(updatedStation.getLocation());
        }
        if (updatedStation.getStatus()!=null) {
            station.setStatus(updatedStation.getStatus());
        }
        if (updatedStation.getPowerCapacity()!=null) {
            station.setPowerCapacity(updatedStation.getPowerCapacity());
        }
        if (updatedStation.getAvailableSpots()!=null) {
            station.setAvailableSpots(updatedStation.getAvailableSpots());
        }
        if (updatedStation.getBookingAvailabel()!=null) {
            station.setBookingAvailabel(updatedStation.getBookingAvailabel());
        }
        if (updatedStation.getLatitude()!=null) {
            station.setLatitude(updatedStation.getLatitude());
        }
        if (updatedStation.getLongitude()!=null) {
            station.setLongitude(updatedStation.getLongitude());
        }
        return chargingStationRepository.save(station);
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
