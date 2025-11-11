package com.group3.evproject.repository;

import com.group3.evproject.entity.ChargingSession;
import com.group3.evproject.entity.ChargingSpot;
import com.group3.evproject.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {

    List<ChargingSession> findByBooking_Vehicle_IdOrderByStartTimeDesc(Long vehicleId);

    boolean existsBySpotAndStatus( ChargingSpot spot,ChargingSession.Status status);

    boolean existsByBookingAndStatus(Booking booking, ChargingSession.Status status);

    List<ChargingSession> findBySpotId (Long spotId);

    @Query("SELECT COALESCE(SUM(cs.totalCost), 0) FROM ChargingSession cs WHERE cs.station.id = :stationId")
    Double getTotalSessionRevenueByStation(Long stationId);
}
