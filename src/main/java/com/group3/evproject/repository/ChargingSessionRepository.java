package com.group3.evproject.repository;

import com.group3.evproject.entity.ChargingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

    boolean existsByBooking_IdAndStatus(Long bookingId, ChargingSession.SessionStatus status);

    List<ChargingSession> findByStation_Id(Long stationId);

    List<ChargingSession> findBySpot_Id(Long spotId);

    Optional<ChargingSession> findByBooking_IdAndStatus(Long bookingId, ChargingSession.SessionStatus status);

    List<ChargingSession> findByStatus(ChargingSession.SessionStatus status);
}
