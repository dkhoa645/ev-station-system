package com.group3.evproject.repository;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.ChargingStation;
import com.group3.evproject.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

    List<ChargingSession> findByStation(ChargingStation station);

    List<ChargingSession> findByBooking(Booking booking);

    List<ChargingSession> findByStatus(ChargingSession.Status status);
}
