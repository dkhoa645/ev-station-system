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
    private final AuthenticationService authenticationService;
    private final VehicleRepository vehicleRepository;

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

    public Booking createBooking(Long stationId, LocalDateTime timeToCharge, LocalDateTime endTime, Long userId, Long vehicleId) {

        //lay thong tin user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //lay thong tin vehicle
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        //lay thong tin station
        ChargingStation station = chargingStationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        //check tra slot kha dung
        if (station.getBookingAvailable() == null || station.getBookingAvailable() <= 0) {
            throw new RuntimeException("No booking slots available for this station");
        }

        //check tra time hợp lệ
        LocalDateTime now = LocalDateTime.now();
        if (timeToCharge.isBefore(now)) {
            throw new RuntimeException("Charging time must be in the future");
        }
        if (endTime.isBefore(timeToCharge)) {
            throw new RuntimeException("End time must be after charging start time");
        }

        //tim cho sac trong
        ChargingSpot spot = chargingSpotRepository.findFirstByStationAndAvailableTrue(station)
                .orElseThrow(() -> new RuntimeException("No available charging spots at this station"));

        //Giảm slot khả dụng
        station.setBookingAvailable(station.getBookingAvailable() - 1);
        if (station.getAvailableSpots() != null && station.getAvailableSpots() > 0) {
            station.setAvailableSpots(station.getAvailableSpots() - 1);
        }
        chargingStationRepository.save(station);

        //Tạo booking mới
        Booking booking = Booking.builder()
                .user(user)
                .station(station)
                .vehicle(vehicle)
                .spot(spot)
                .timeToCharge(timeToCharge)
                .endTime(endTime)
                .status(Booking.BookingStatus.PENDING)
                .totalCost(0.0)
                .updatedAt(now)
                .build();
        return bookingRepository.save(booking);
    }

    // Update booking
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking booking = getBookingById(id);

        if (updatedBooking.getTimeToCharge() != null) {
            booking.setTimeToCharge(updatedBooking.getTimeToCharge());
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
        if (booking.getStatus() == Booking.BookingStatus.COMPLETED || booking.getStatus() == Booking.BookingStatus.CANCELLED){
            throw  new RuntimeException("Booking is already completed or cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());

        ChargingStation station = booking.getStation();
        ChargingSpot spot = booking.getSpot();
        if (spot != null) {
            spot.setStatus("AVAILABLE");
            spot.setAvailable(true);
            chargingSpotRepository.save(spot);
        }
        if (station != null) {
            if (station.getBookingAvailable() != null) {
                station.setBookingAvailable(station.getBookingAvailable() + 1);
            }
            if (station.getAvailableSpots() != null) {
                station.setAvailableSpots(station.getAvailableSpots() + 1);
            }
            chargingStationRepository.save(station);
        }
        return bookingRepository.save(booking);
    }

    // Confirm booking
    public Booking confirmBooking(Long id) {
        Booking booking = getBookingById(id);
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be confirmed.");
        }
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking completeBooking(Long id) {
        Booking booking = getBookingById(id);
        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED
                && booking.getStatus() != Booking.BookingStatus.CHARGING) {
            throw new RuntimeException("Only confirmed or charging bookings can be completed.");
        }
        booking.setStatus(Booking.BookingStatus.COMPLETED);
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

