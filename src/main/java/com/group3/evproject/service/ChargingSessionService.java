package com.group3.evproject.service;
import com.group3.evproject.entity.Booking;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.repository.ChargingSessionRepository;
import com.group3.evproject.repository.BookingRepository;
import com.group3.evproject.repository.ChargingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChargingSessionService {
    private final ChargingSessionRepository chargingSessionRepository;
    private final BookingRepository bookingRepository;
    private final ChargingSpotRepository chargingSpotRepository;

    public List<ChargingSession> getAllSessions() {
        return chargingSessionRepository.findAll();
    }

    public ChargingSession getSessionById(Long id) {
        return chargingSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging session not found with id: " + id));
    }

    public ChargingSession createSession(ChargingSession session) {
        if(session.getStartTime() == null) {
            session.setStartTime(LocalDateTime.now());
        }
        session.setStatus(ChargingSession.SessionStatus.IN_PROGRESS);
        return chargingSessionRepository.save(session);
    }

    public  ChargingSession updateSession(ChargingSession updatedSession, Long id) {
        ChargingSession existing = chargingSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging session not found with id: " + id));
        existing.setEndTime(updatedSession.getEndTime());
        existing.setEnergyUsed(updatedSession.getEnergyUsed());
        existing.setTotalCost(updatedSession.getTotalCost());
        existing.setStatus(updatedSession.getStatus());
        return chargingSessionRepository.save(existing);
    }

    public void cancelSession(Long id) {
        ChargingSession existing = chargingSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging session not found with id: " + id));
        existing.setStatus(ChargingSession.SessionStatus.CANCELLED);
        chargingSessionRepository.save(existing);
    }

    public List<ChargingSession> getSessionsByBooking(Long bookingId) {
        return chargingSessionRepository.findByBooking_Id(bookingId);
    }

    public List<ChargingSession> getSessionsByStation(Long stationId) {
        return chargingSessionRepository.findByStation_Id(stationId);
    }

    public List<ChargingSession> getSessionsByStatus(String status) {
        return chargingSessionRepository.findByStatus(status);
    }

    public ChargingSession checkIn(Long bookId,Long spotId){
        Booking booking = bookingRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookId));
        ChargingSpot spot = chargingSpotRepository.findById(spotId).orElseThrow(() -> new RuntimeException("Spot not found with id: " + spotId));

        //update spot va booking
        spot.setAvailable(false);
        spot.setStatus("CHARGING");
        chargingSpotRepository.save(spot);

        booking.setStatus(Booking.BookingStatus.CHARGING);
        bookingRepository.save(booking);

        ChargingSession session = ChargingSession.builder()
                .booking(booking)
                .station(booking.getStation())
                .spot(spot)
                .startTime(LocalDateTime.now())
                .status(ChargingSession.SessionStatus.IN_PROGRESS)
                .build();
        return chargingSessionRepository.save(session);
    }


}
