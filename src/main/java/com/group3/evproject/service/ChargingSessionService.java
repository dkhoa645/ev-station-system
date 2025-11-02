package com.group3.evproject.service;

import com.group3.evproject.entity.*;
import com.group3.evproject.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.group3.evproject.dto.response.ChargingSessionResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargingSessionService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final ChargingSpotRepository chargingSpotRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final BookingRepository bookingRepository;

    public List<ChargingSession> getAllSessions() {
        return chargingSessionRepository.findAll();
    }
    public ChargingSession getSessionEntityById(Long id) {
        return chargingSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charging session not found with id: " + id));
    }

    public ChargingSessionResponse getSessionById(Long id) {
        ChargingSession session = getSessionEntityById(id);

        return ChargingSessionResponse.builder()
                .sessionId(session.getId())
                .stationName(session.getStation() != null ? session.getStation().getName() : null)
                .spotName(session.getSpot() != null ? session.getSpot().getSpotName() : null)
                .bookingId(session.getBooking() != null ? session.getBooking().getId() : null)
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .chargingDuration(session.getChargingDuration())
                .powerOutput(session.getPowerOutput())
                .batteryCapacity(session.getBatteryCapacity())
                .percentBefore(session.getPercentBefore())
                .percentAfter(session.getPercentAfter())
                .energyUsed(session.getEnergyUsed())
                .ratePerKWh(session.getRatePerKWh())
                .totalCost(session.getTotalCost())
                .status(session.getStatus().name())
                .build();
    }

    public ChargingSession startSession(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        ChargingStation station = booking.getStation();

        // Tìm spot khả dụng
        ChargingSpot spot = chargingSpotRepository.findFirstByStationAndStatus(station, ChargingSpot.SpotStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("No available charging spots at this station"));

        // Đánh dấu spot đang bận
        spot.setStatus(ChargingSpot.SpotStatus.OCCUPIED);
        chargingSpotRepository.save(spot);

        // Giảm availableSpot trong station
        station.setAvailableSpots(Math.max(0, station.getAvailableSpots() - 1));
        chargingStationRepository.save(station);

        // Tạo session
        ChargingSession session = ChargingSession.builder()
                .booking(booking)
                .station(station)
                .spot(spot)
                .startTime(LocalDateTime.now())
                .powerOutput(station.getPowerCapacity()) // dùng công suất trạm
                .status(ChargingSession.Status.ACTIVE)
                .build();

        return chargingSessionRepository.save(session);
    }

    public ChargingSession endSession(Long sessionId, Double ratePerKWh, Double percentBefore, Double batteryCapacity) {
        ChargingSession session =getSessionEntityById(sessionId);

        if (session.getStatus() != ChargingSession.Status.ACTIVE) {
            throw new RuntimeException("Only active sessions can be ended.");
        }

        LocalDateTime endTime = LocalDateTime.now();
        session.setEndTime(endTime);

        // 🔹 1. Tính thời gian sạc (giờ)
        double durationHours = Duration.between(session.getStartTime(), endTime).toMinutes() / 60.0;
        session.setChargingDuration(durationHours);

        // 🔹 2. Tính số điện đã vào xe (kWh)
        double energyAdded = session.getPowerOutput() * durationHours;
        session.setEnergyAdded(energyAdded);

        // 🔹 3. Tính % sau sạc
        double percentAfter = ((energyAdded / batteryCapacity) * 100) + percentBefore;
        if (percentAfter > 100) percentAfter = 100.0;
        session.setPercentBefore(percentBefore);
        session.setPercentAfter(percentAfter);
        session.setBatteryCapacity(batteryCapacity);

        // 🔹 4. Lượng điện đã sạc (kWh)
        double energyUsed = (percentAfter - percentBefore) * (batteryCapacity / 100);
        session.setEnergyUsed(energyUsed);

        // 🔹 5. Tính chi phí sạc
        session.setRatePerKWh(ratePerKWh);
        double totalCost = energyUsed * ratePerKWh;
        session.setTotalCost(Double.valueOf(totalCost));

        // 🔹 6. Cập nhật trạng thái
        session.setStatus(ChargingSession.Status.COMPLETED);

        // 🔹 7. Giải phóng spot
        ChargingSpot spot = session.getSpot();
        spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
        chargingSpotRepository.save(spot);

        // 🔹 8. Tăng available spot trong station
        ChargingStation station = session.getStation();
        station.setAvailableSpots(Math.min(station.getTotalSpots(), station.getAvailableSpots() + 1));
        chargingStationRepository.save(station);

        // 🔹 9. Cập nhật booking tương ứng
        Booking booking = session.getBooking();
        if (booking != null) {
            booking.setStatus(Booking.BookingStatus.COMPLETED);
            booking.setTotalCost(session.getTotalCost());
            bookingRepository.save(booking);
        }

        return chargingSessionRepository.save(session);
    }

    public ChargingSession cancelSession(Long sessionId) {
        ChargingSession session = getSessionEntityById(sessionId);

        if (session.getStatus() == ChargingSession.Status.COMPLETED) {
            throw new RuntimeException("Completed session cannot be cancelled.");
        }

        session.setStatus(ChargingSession.Status.CANCELLED);
        session.setEndTime(LocalDateTime.now());

        // Giải phóng spot
        ChargingSpot spot = session.getSpot();
        if (spot != null) {
            spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
            chargingSpotRepository.save(spot);
        }

        // Cập nhật station
        ChargingStation station = session.getStation();
        if (station != null) {
            station.setAvailableSpots(Math.min(station.getTotalSpots(), station.getAvailableSpots() + 1));
            chargingStationRepository.save(station);
        }

        return chargingSessionRepository.save(session);
    }
}
