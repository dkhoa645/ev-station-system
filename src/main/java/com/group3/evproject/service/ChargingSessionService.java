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

    public ChargingSession startSession(Long bookingId, Long spotId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("⚠️ Booking not found with id: " + bookingId));

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking must be CONFIRMED before starting a session.");
        }

        ChargingStation station = booking.getStation();
        if (station == null) {
            throw new RuntimeException("Booking does not have an assigned station.");
        }

        //Lấy spot mà người dùng đã chọn
        ChargingSpot spot = chargingSpotRepository.findById(spotId)
                .orElseThrow(() -> new RuntimeException("pot not found with id: " + spotId));

        // Kiểm tra xem spot có thuộc về đúng station không
        if (!spot.getStation().getId().equals(station.getId())) {
            throw new RuntimeException("Spot does not belong to this station.");
        }

        // Kiểm tra trạng thái spot
        if (spot.getStatus() != ChargingSpot.SpotStatus.AVAILABLE) {
            throw new RuntimeException("Selected spot is not available.");
        }

        // Đánh dấu spot là đang được sử dụng
        spot.setStatus(ChargingSpot.SpotStatus.OCCUPIED);
        chargingSpotRepository.save(spot);

        // Giảm số chỗ trống trong station
        station.setAvailableSpots(Math.max(0, station.getAvailableSpots() - 1));
        chargingStationRepository.save(station);

        // Cập nhật trạng thái booking
        booking.setStatus(Booking.BookingStatus.CHARGING);
        bookingRepository.save(booking);

        // Tạo session
        ChargingSession session = ChargingSession.builder()
                .booking(booking)
                .station(station)
                .spot(spot)
                .startTime(LocalDateTime.now())
                .powerOutput(spot.getPowerOutput() != null ? spot.getPowerOutput() : station.getPowerCapacity())
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

        //Tính thời gian sạc (giờ)
        double durationHours = Duration.between(session.getStartTime(), endTime).toMinutes() / 60.0;
        session.setChargingDuration(durationHours);

        //Tính số điện đã vào xe (kWh)
        double energyAdded = session.getPowerOutput() * durationHours;
        session.setEnergyAdded(energyAdded);

        //Tính % sau sạc
        double percentAfter = ((energyAdded / batteryCapacity) * 100) + percentBefore;
        if (percentAfter > 100) percentAfter = 100.0;
        session.setPercentBefore(percentBefore);
        session.setPercentAfter(percentAfter);
        session.setBatteryCapacity(batteryCapacity);

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

        //Tăng available spot trong station
        ChargingStation station = session.getStation();
        station.setAvailableSpots(Math.min(station.getTotalSpots(), station.getAvailableSpots() + 1));
        chargingStationRepository.save(station);

        //Cập nhật booking tương ứng
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
