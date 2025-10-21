package com.group3.evproject.service;

import com.group3.evproject.entity.*;
import com.group3.evproject.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final ChargingSpotRepository chargingSpotRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    // lay user tu token
    User user = authenticationService.extractUsernameFromRequest();

    // Lấy tất cả booking
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    // Lấy booking theo ID
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    // Lấy bookings theo userId
    public List<Booking> getBookingByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No user found for user id: " + userId));
        return bookingRepository.findByUser(user);
    }

    // Lấy bookings theo stationId
    public List<Booking> getBookingByStation(Long stationId) {
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("No station found for station id: " + stationId));
        return bookingRepository.findByStation(station);
    }

    public Booking createBooking(Long stationId, LocalDate startDate, LocalDate endDate, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("No user found for user id: " + userId));

        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("No station found for station id: " + stationId));

        ChargingSpot availableSpot = chargingSpotRepository
                .findFirstByStationAndAvailableTrue(station)
                .orElseThrow(() -> new RuntimeException("No available spots at this station"));

        availableSpot.setAvailable(false);
        chargingSpotRepository.save(availableSpot);

        Booking booking = Booking.builder()
                .user(user)
                .station(station)
                .spot(availableSpot)
                .startTime(startDate.atStartOfDay())
                .endTime(endDate.atStartOfDay())
                .status(Booking.BookingStatus.PENDING)
                .totalCost(0.0)
                .updatedAt(LocalDateTime.now())
                .build();

        return bookingRepository.save(booking);
    }
    // Update booking
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking booking = getBookingById(id);

        if (updatedBooking.getStartTime() != null) {
            booking.setStartTime(updatedBooking.getStartTime());
        }
        if (updatedBooking.getEndTime() != null) {
            booking.setEndTime(updatedBooking.getEndTime());
        }
        if (updatedBooking.getTotalCost() != null) {
            booking.setTotalCost(updatedBooking.getTotalCost());
        }
        booking.setUpdatedAt(LocalDateTime.now());

        return bookingRepository.save(booking);
    }

    // Cancel booking
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        if (booking.getSpot() != null) {
            booking.getSpot().setAvailable(true);
            chargingSpotRepository.save(booking.getSpot());
        }

        return bookingRepository.save(booking);
    }

    // Confirm booking
    public Booking confirmBooking(Long id) {
        Booking booking = getBookingById(id);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    // Delete booking
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);

        if (booking.getSpot() != null) {
            booking.getSpot().setAvailable(true);
            chargingSpotRepository.save(booking.getSpot());
        }

        bookingRepository.delete(booking);
    }
}

