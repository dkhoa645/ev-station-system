package com.group3.evproject.service;

import com.group3.evproject.entity.*;
import com.group3.evproject.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.group3.evproject.dto.response.ChargingSessionResponse;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChargingSessionService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final ChargingSpotRepository chargingSpotRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final VehicleRepository vehicleRepository;

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

    private void createInvoiceForSession(ChargingSession session) {
        Invoice invoice = new Invoice();
        invoice.setSession(session);
        invoice.setIssueDate(LocalDateTime.now());
        invoice.setFinalCost(BigDecimal.valueOf(session.getTotalCost()));
        invoice.setStatus(Invoice.Status.PENDING);

        SubscriptionPlan plan = null;
        if (session.getBooking() != null && session.getBooking().getVehicle() != null) {
            Vehicle vehicle = session.getBooking().getVehicle();
            if (vehicle.getSubscription() != null &&
                    vehicle.getSubscription().getSubscriptionPlan() != null) {
                plan = vehicle.getSubscription().getSubscriptionPlan();
            }
        }

        if (plan != null) {
            invoice.setSubscriptionPlan(plan);
        }

        invoiceRepository.save(invoice);
    }

    public ChargingSession startSession(Long bookingId, Long spotId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        ChargingStation station = booking.getStation();

        // Tìm spot khả dụng
        ChargingSpot spot = chargingSpotRepository.findFirstByStationAndStatus(station, ChargingSpot.SpotStatus.AVAILABLE)
                .orElseThrow(() -> new RuntimeException("No available charging spots at this station"));

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

        createInvoiceForSession(session);
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
