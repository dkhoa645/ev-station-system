package com.group3.evproject.service;

import com.group3.evproject.dto.request.BookingRequest;
import com.group3.evproject.dto.response.BookingResponse;
import com.group3.evproject.entity.*;
import com.group3.evproject.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ChargingStationRepository chargingStationRepository;
    private final ChargingSpotRepository chargingSpotRepository;
    private final ChargingSpotService chargingSpotService;
    private final VehicleRepository vehicleRepository;
    private static final double fee = 30000;

    // Lấy tất cả booking
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking findBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Booking not found"));
    }

    // Lấy booking theo ID
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Booking not found"));
        BookingResponse response = BookingResponse.builder()
                .bookingId(booking.getId())
                .reservationFee(booking.getReservationFee())
                .vehicleId(booking.getVehicle().getId())
                .status(booking.getStatus())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .timeToCharge(booking.getTimeToCharge())
                .stationName(booking.getStation().getName())
                .build();

        return response;
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

    // Tạo booking mới (bỏ availableSpot logic)
    public Booking createBooking(BookingRequest request) {

        // Lấy thông tin
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        ChargingStation station = chargingStationRepository.findById(request.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));

        LocalDateTime timeToCharge = request.getTimeToCharge();
        LocalDateTime endTime = request.getEndTime();

        // Kiểm tra thời gian hợp lệ
        LocalDateTime now = LocalDateTime.now();
        if (timeToCharge.isBefore(now)) {
            throw new RuntimeException("Charging time must be in the future");
        }
        if (endTime.isBefore(timeToCharge)) {
            throw new RuntimeException("End time must be after charging start time");
        }

        //kiểm tra available spot
        int availableSpots = chargingSpotService.getAvailableSpots(station);
        if(availableSpots <= 0) {
            throw new RuntimeException("No available charging spots at this station");
        }

        //tìm spot available và chuyển thành occupied
        ChargingSpot spot = chargingSpotService.getAvailableSpot(station);
        spot.setStatus(ChargingSpot.SpotStatus.OCCUPIED);
        chargingSpotRepository.save(spot);

        // Tính phí
        double hours = Duration.between(timeToCharge, endTime).toMinutes() / 60.0;
        double reservationFee = hours * fee;

        // Tạo booking mới
        Booking booking = Booking.builder()
                .user(user)
                .station(station)
                .spot(spot)
                .vehicle(vehicle)
                .paymentTransactions(new ArrayList<>())
                .timeToCharge(timeToCharge)
                .endTime(endTime)
                .status(Booking.BookingStatus.PENDING)
                .reservationFee(BigDecimal.valueOf(reservationFee))
                .build();

        //update availableSpots của station
        station.setAvailableSpots(station.getAvailableSpots() + 1);
        chargingStationRepository.save(station);

        return bookingRepository.save(booking);
    }

    // Update booking
    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking booking = findBookingById(id);

        if (updatedBooking.getTimeToCharge() != null) {
            booking.setTimeToCharge(updatedBooking.getTimeToCharge());
        }
        if (updatedBooking.getEndTime() != null) {
            booking.setEndTime(updatedBooking.getEndTime());
        }
        if (updatedBooking.getTotalCost() != null) {
            booking.setTotalCost(updatedBooking.getTotalCost());
        }
        return bookingRepository.save(booking);
    }

    // Cancel booking (bỏ logic availableSpot)
    public Booking cancelBooking(Long id) {
        Booking booking = findBookingById(id);

        if (booking.getStatus() == Booking.BookingStatus.COMPLETED || booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already completed or cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);

        //trả spot về available
        ChargingSpot spot = booking.getSpot();
        if (spot != null) {
            spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
            chargingSpotRepository.save(spot);
        }

        //update lại spot available
        ChargingStation station = booking.getStation();
        station.setAvailableSpots(chargingSpotService.getAvailableSpots(station));
        chargingStationRepository.save(station);

        return bookingRepository.save(booking);
    }

    // Confirm booking
    public Booking confirmBooking(Long id) {
        Booking booking = findBookingById(id);
        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be confirmed.");
        }
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    // Complete booking (bỏ logic availableSpot)
    public Booking completeBooking(Long id) {
        Booking booking = findBookingById(id);

        if (booking.getStatus() != Booking.BookingStatus.CONFIRMED
                && booking.getStatus() != Booking.BookingStatus.CHARGING) {
            throw new RuntimeException("Only confirmed or charging bookings can be completed.");
        }
        booking.setStatus(Booking.BookingStatus.COMPLETED);

        //giải phóng spot
        ChargingSpot spot = booking.getSpot();
        if (spot != null) {
            spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
            chargingSpotRepository.save(spot);
        }

        // Cập nhật lại số chỗ trống
        ChargingStation station = booking.getStation();
        station.setAvailableSpots(chargingSpotService.getAvailableSpots(station));
        chargingStationRepository.save(station);

        return bookingRepository.save(booking);
    }

    // Delete booking
    public void deleteBooking(Long id) {
        Booking booking = findBookingById(id);
        ChargingSpot spot = booking.getSpot();

        if (spot != null) {
            spot.setStatus(ChargingSpot.SpotStatus.AVAILABLE);
            chargingSpotRepository.save(spot);
        }

        ChargingStation station = booking.getStation();
        station.setAvailableSpots(chargingSpotService.getAvailableSpots(station));
        chargingStationRepository.save(station);

        bookingRepository.delete(booking);
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
}
