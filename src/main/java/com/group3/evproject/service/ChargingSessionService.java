package com.group3.evproject.service;
import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.repository.ChargingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChargingSessionService {
    private final ChargingSessionRepository chargingSessionRepository;

    public List<ChargingSession> getAllSessions() {
        return chargingSessionRepository.findAll();
    }

    public ChargingSession getSessionById(Integer id) {
        return chargingSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging session not found with id: " + id));
    }

    public ChargingSession createSession(ChargingSession session) {
        if(session.getStartTime() == null) {
            session.setStartTime(LocalDateTime.now());
        }
        session.setStatus(("IN_PROGRESS"));
        return chargingSessionRepository.save(session);
    }

    public  ChargingSession updateSession(ChargingSession updatedSession, Integer id) {
        ChargingSession existing = chargingSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging session not found with id: " + id));
        existing.setEndTime(updatedSession.getEndTime());
        existing.setEnergyUsed(updatedSession.getEnergyUsed());
        existing.setTotalCost(updatedSession.getTotalCost());
        existing.setStatus(updatedSession.getStatus());
        return chargingSessionRepository.save(existing);
    }

    public void cancelSession(Integer id) {
        ChargingSession existing = chargingSessionRepository.findById(id).orElseThrow(() -> new RuntimeException("Charging session not found with id: " + id));
        existing.setStatus("CANCELLED");
        chargingSessionRepository.save(existing);
    }

    public List<ChargingSession> getSessionsByBooking(Integer bookingId) {
        return chargingSessionRepository.findByBooking_Id(bookingId);
    }

    public List<ChargingSession> getSessionsByStation(Integer stationId) {
        return chargingSessionRepository.findByStation_Id(stationId);
    }

    public List<ChargingSession> getSessionsByStatus(String status) {
        return chargingSessionRepository.findByStatus(status);
    }
}
