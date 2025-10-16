package com.group3.evproject.service;
import com.group3.evproject.entity.Booking;
import com.group3.evproject.repository.BookingRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private  final BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {
        if(booking.getStatus() == null){
            booking.setStatus("BOOKED");
        }
        booking.setStartTime(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Integer id, Booking updatedBooking) {
        Optional<Booking> existing = bookingRepository.findById(id);
        if(existing.isPresent()){
            Booking booking = existing.get();
            booking.setStation(updatedBooking.getStation());
            booking.setVehicle(updatedBooking.getVehicle());
            booking.setSpot(updatedBooking.getSpot());
            booking.setStartTime(updatedBooking.getStartTime());
            booking.setEndTime(updatedBooking.getEndTime());
            booking.setStatus(updatedBooking.getStatus());
            return bookingRepository.save(booking);
        } else{
            throw new RuntimeException("Booking not found with id: " + id);
        }
    }

    public void cancelBooking(Integer id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Integer id) {
        return bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    public List<Booking> getBookingsByUserId(Integer userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByStationId(Integer stationId) {
        return bookingRepository.findByStationId(stationId);
    }

    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    public List<Booking> getBookingBetween(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByStartTimeBetween(start, end);
    }

    public boolean checkActiveBooking(Integer userId) {
        List<Booking> activeBookings = bookingRepository.findByUserIdAndStatus(userId, "active");
        return !activeBookings.isEmpty();
    }
}
