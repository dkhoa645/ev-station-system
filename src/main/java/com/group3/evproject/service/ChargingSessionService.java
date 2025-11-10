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
    private final BookingRepository bookingRepository;
    private final InvoiceService invoiceService;

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

    public ChargingSession startSession(Long bookingId, Long spotId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        ChargingStation station = booking.getStation();

        if (chargingSessionRepository.existsByBookingAndStatus(booking, ChargingSession.Status.ACTIVE)) {
            throw new RuntimeException("Booking is already active.");
        }

        // Tìm spot khả dụng
        ChargingSpot spot = chargingSpotRepository.findById(spotId).orElseThrow(() -> new RuntimeException("Spot not found with id: " + spotId));

        if (chargingSessionRepository.existsBySpotAndStatus(spot, ChargingSession.Status.ACTIVE)) {
            throw new RuntimeException("Spot is already in use.");
        }

        if (spot.getStatus() != ChargingSpot.SpotStatus.AVAILABLE) {
            throw new RuntimeException("Spot is not available.");
        }

        // Tạo session
        ChargingSession session = ChargingSession.builder()
                .booking(booking)
                .station(station)
                .spot(spot)
                .startTime(LocalDateTime.now())
                .powerOutput(station.getPowerCapacity()) // dùng công suất trạm
                .status(ChargingSession.Status.ACTIVE)
                .build();

        if (spot.getSpotType() == ChargingSpot.SpotType.WALK_IN) {
            spot.setStatus(ChargingSpot.SpotStatus.OCCUPIED);
            chargingSpotRepository.save(spot);
        }

        return chargingSessionRepository.save(session);
    }

    public ChargingSession endSession(Double batteryCapacity, Double ratePerKWh, Long sessionId, Double percentBefore) {

        ChargingSession session = getSessionEntityById(sessionId);

        if (session.getStatus() != ChargingSession.Status.ACTIVE) {
            throw new RuntimeException("Only active sessions can be ended.");
        }

        LocalDateTime endTime = LocalDateTime.now();
        session.setEndTime(endTime);

        //Tính thời gian sạc (giờ)
        double durationHours = Duration.between(session.getStartTime(), endTime).toSeconds() / 3600.0;
        session.setChargingDuration(durationHours);

        //Tính số điện đã vào xe (kWh)
        double energyAdded = session.getPowerOutput() * durationHours;
        session.setEnergyAdded(energyAdded);

        //Tính % sau sạc
        double percentAfter = ((energyAdded / batteryCapacity) * 100) + percentBefore;
        if (percentAfter > 100) percentAfter = 100.0;
        session.setPercentBefore(percentBefore);
        session.setPercentAfter(percentAfter);
        session.setBatteryCapacity(Double.valueOf(batteryCapacity));

        //Lượng điện đã sạc (kWh)
        double energyUsed = (percentAfter - percentBefore) * (batteryCapacity / 100);
        session.setEnergyUsed(energyUsed);

        //Tính chi phí sạc
        session.setRatePerKWh(ratePerKWh);
        double totalCost = energyUsed * ratePerKWh;
        session.setTotalCost(Double.valueOf(totalCost));

        //Cập nhật trạng thái
        session.setStatus(ChargingSession.Status.COMPLETED);

        //Giải phóng spot
        ChargingSpot spot = session.getSpot();
        spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
        chargingSpotRepository.save(spot);

        //Cập nhật booking tương ứng
        Booking booking = session.getBooking();
        if (booking != null) {
            booking.setStatus(Booking.BookingStatus.COMPLETED);
            booking.setTotalCost(session.getTotalCost());
            bookingRepository.save(booking);
        }

        invoiceService.createInvoiceBySessionId(sessionId);
        return chargingSessionRepository.save(session);
    }

    public ChargingSession startSessionForStaff (Long spotId,Double percentBefore) {

        // Tìm spot khả dụng
        ChargingSpot spot = chargingSpotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("No available charging spots at this station"));

        if (chargingSessionRepository.existsBySpotAndStatus(spot, ChargingSession.Status.ACTIVE)) {
            throw new RuntimeException("This spot already has an active charging session.");
        }

        if (spot.getStatus() != ChargingSpot.SpotStatus.AVAILABLE) {
            throw new RuntimeException("Spot is not available");
        }

        ChargingStation station = spot.getStation();

        // Tạo session
        ChargingSession session = ChargingSession.builder()
                .spot(spot)
                .station(station)
                .startTime(LocalDateTime.now())
                .powerOutput(station.getPowerCapacity())
                .percentBefore(percentBefore)
                .status(ChargingSession.Status.ACTIVE)
                .build();

        if (spot.getSpotType() == ChargingSpot.SpotType.WALK_IN) {
            spot.setStatus(ChargingSpot.SpotStatus.OCCUPIED);
            chargingSpotRepository.save(spot);
        }

        return chargingSessionRepository.save(session);
    }

    public ChargingSession endSessionForStaff (Double batteryCapacity, Double ratePerKWh, Long sessionId, Double percentBefore) {

        ChargingSession session = getSessionEntityById(sessionId);

        if (session.getStatus() != ChargingSession.Status.ACTIVE) {
            throw new RuntimeException("Only active sessions can be ended.");
        }

        LocalDateTime endTime = LocalDateTime.now();
        session.setEndTime(endTime);

        //Tính thời gian sạc (giờ)
        double durationHours = Duration.between(session.getStartTime(), endTime).toSeconds() / 3600.0;
        session.setChargingDuration(durationHours);

        //Tính số điện đã vào xe (kWh)
        double energyAdded = session.getPowerOutput() * durationHours;
        session.setEnergyAdded(energyAdded);

        //Tính % sau sạc
        double percentAfter = ((energyAdded / batteryCapacity) * 100) + percentBefore;
        if (percentAfter > 100) percentAfter = 100.0;
        session.setPercentBefore(percentBefore);
        session.setPercentAfter(percentAfter);
        session.setBatteryCapacity(Double.valueOf(batteryCapacity));

        //Lượng điện đã sạc (kWh)
        double energyUsed = (percentAfter - percentBefore) * (batteryCapacity / 100);
        session.setEnergyUsed(energyUsed);

        //Tính chi phí sạc
        session.setRatePerKWh(ratePerKWh);
        double totalCost = energyUsed * ratePerKWh;
        session.setTotalCost(Double.valueOf(totalCost));

        //Cập nhật trạng thái
        session.setStatus(ChargingSession.Status.COMPLETED);

        //Giải phóng spot
        ChargingSpot spot = session.getSpot();
        spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
        chargingSpotRepository.save(spot);

        invoiceService.createInvoiceBySessionId(sessionId);

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

        return chargingSessionRepository.save(session);
    }

    public List<ChargingSessionResponse> getSessionsByVehicle(Long vehicleId) {
        List<ChargingSession> sessions = chargingSessionRepository
                .findByBooking_Vehicle_IdOrderByStartTimeDesc(vehicleId);

        return sessions.stream()
                .map(session -> ChargingSessionResponse.builder()
                        .sessionId(session.getId())
                        .stationName(session.getStation() != null ? session.getStation().getName() : null)
                        .spotName(session.getSpot() != null ? session.getSpot().getSpotName() : null)
                        .startTime(session.getStartTime())
                        .endTime(session.getEndTime())
                        .energyUsed(session.getEnergyUsed())
                        .totalCost(session.getTotalCost())
                        .status(session.getStatus().name())
                        .build())
                .toList();
    }

}
