package com.group3.evproject.service;

import com.group3.evproject.entity.Booking;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.repository.BookingRepository;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.ChargingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChargingSessionService {

    private final ChargingSessionRepository chargingSessionRepository;
    private final BookingRepository bookingRepository;
    private final ChargingSpotRepository spotRepository;

    //start
    public ChargingSession startSession(Long bookingId, Long chargingPointId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        ChargingSpot spot = spotRepository.findById(chargingPointId)
                .orElseThrow(() -> new RuntimeException("Charging point not found"));

        // Kiểm tra thời gian hợp lệ
        if (booking.getStartTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cannot start charging before your booking time");
        }

        // Kiểm tra điểm sạc có đang trống
        if (spot.getStatus() == ChargingSpot.SpotStatus.OCCUPIED) {
            throw new RuntimeException("Charging point is already occupied");
        }

        // Đánh dấu điểm sạc đang sử dụng
        spot.setStatus(ChargingSpot.SpotStatus.OCCUPIED);
        spotRepository.save(spot);

        // Tạo session mới
        ChargingSession session = ChargingSession.builder()
                .booking(booking)
                .station(booking.getStation())
                .spot(spot)
                .startTime(LocalDateTime.now())
                .status(ChargingSession.SessionStatus.IN_PROGRESS)
                .build();

        return chargingSessionRepository.save(session);
    }

    //end
    public ChargingSession endSession(Long sessionId, double batteryEnd, double energyUsed) {
        ChargingSession session = chargingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Charging session not found"));

        if (session.getStatus() != ChargingSession.SessionStatus.IN_PROGRESS) {
            throw new RuntimeException("Session is not currently active");
        }

        session.setEndTime(LocalDateTime.now());
        session.setBatteryEnd(batteryEnd);
        session.setEnergyUsed(energyUsed);

        // Tính thời lượng
        long minutes = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        session.setDurationMinutes((int) minutes);
        session.setChargingDuration((double) minutes);

        // Giá điện giả định
        double pricePerKwh = 0.25;
        session.setTotalCost(energyUsed * pricePerKwh);

        session.setStatus(ChargingSession.SessionStatus.COMPLETED);

        // Giải phóng điểm sạc
        ChargingSpot spot = session.getSpot();
        spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
        spotRepository.save(spot);

        return chargingSessionRepository.save(session);
    }

    //cancel
    public ChargingSession cancelSession(Long sessionId) {
        ChargingSession session = chargingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Charging session not found"));

        if (session.getStatus() == ChargingSession.SessionStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed session");
        }

        session.setStatus(ChargingSession.SessionStatus.CANCELLED);

        // Giải phóng điểm sạc nếu đang bị giữ
        ChargingSpot spot = session.getSpot();
        if (spot.getStatus() == ChargingSpot.SpotStatus.OCCUPIED) {
            spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
            spotRepository.save(spot);
        }

        return chargingSessionRepository.save(session);
    }

    //Lấy session theo ID
    public ChargingSession getSessionById(Long sessionId) {
        return chargingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
    }
}
