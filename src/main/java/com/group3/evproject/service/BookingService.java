// java
package com.group3.evproject.service;

import com.group3.evproject.entity.*;
import com.group3.evproject.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final ChargingSpotRepository chargingSpotRepository;

    public Booking createBooking(Integer stationId, LocalDateTime startTime, LocalDateTime endTime, Integer userId) {
        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        // guard against null spots
        Optional<ChargingSpot> availableSpot = (Optional.ofNullable(station.getSpots()).orElse(Collections.emptyList()))
                .stream()
                .filter(s -> "AVAILABLE".equals(s.getStatus()))
                .findFirst();

        if (availableSpot.isEmpty()) {
            throw new RuntimeException("No available charging spot at this station");
        }
        ChargingSpot spot = availableSpot.get();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStation(station);
        booking.setSpot(spot);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setStatus("BOOKED");

        bookingRepository.save(booking);

        spot.setStatus("BOOKED");
        chargingSpotRepository.save(spot);
        return booking;
    }
}