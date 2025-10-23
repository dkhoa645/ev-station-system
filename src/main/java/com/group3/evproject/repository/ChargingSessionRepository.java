package com.group3.evproject.repository;
import com.group3.evproject.entity.ChargingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChargingSessionRepository extends  JpaRepository<ChargingSession,Long> {
    List<ChargingSession> findByBooking_Id(Long bookingId);

    List<ChargingSession> findByStation_Id(Long stationId);

    List<ChargingSession> findByStatus(String status);

    List<ChargingSession> findBySpot_Id(Long spotId);

    List<ChargingSession> findByStartTimeBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
