package com.group3.evproject.service;
import com.group3.evproject.dto.response.StationRevenueResponse;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.repository.BookingRepository;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.ChargingStationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChargingStationService {
    private final ChargingStationRepository chargingStationRepository;
    private final ChargingSessionRepository chargingSessionRepository;
    private final BookingRepository bookingRepository;

    public List<ChargingStation> getAllChargingStations() {
        return chargingStationRepository.findAll();
    }

    public ChargingStation getChargingStationById(Long id) {
       return chargingStationRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging station not found with id: " + id));
    }

    public ChargingStation createChargingStation(ChargingStation chargingStation) {

        if (chargingStation.getStatus() == null || chargingStation.getStatus().isEmpty()) {
            chargingStation.setStatus("AVAILABLE");
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
        if (updatedStation.getLatitude()!=null) {
            station.setLatitude(updatedStation.getLatitude());
        }
        if (updatedStation.getLongitude()!=null) {
            station.setLongitude(updatedStation.getLongitude());
        }
        if (updatedStation.getImageUrl()!=null) {
            station.setImageUrl(updatedStation.getImageUrl());
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

    public StationRevenueResponse getStationRevenue(Long stationId) {
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Charging station not found with id: " + stationId));

        Double totalSession = chargingSessionRepository.getTotalSessionRevenueByStation(stationId);
        Double totalBooking = bookingRepository.getTotalBookingRevenueByStation(stationId);
        Double total = totalSession + totalBooking;

        return new StationRevenueResponse(
                stationId,
                totalSession,
                totalBooking
        );
    }
}
