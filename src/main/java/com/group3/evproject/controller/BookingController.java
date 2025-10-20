package com.group3.evproject.controller;

import com.group3.evproject.dto.request.BookingRequest;
import com.group3.evproject.entity.Booking;
import com.group3.evproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

<<<<<<< HEAD
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingByUser(userId));
    }

    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Booking>> getBookingsByStation(@PathVariable Long stationId) {
        return ResponseEntity.ok(bookingService.getBookingByStation(stationId));
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody BookingRequest bookingRequest
    ) {
        Long userId = bookingRequest.getUserId();
        Long stationId = bookingRequest.getStationId().longValue(); // stationId trong DTO là Integer
        LocalDate startDate = bookingRequest.getStartTime().toLocalDate();
        LocalDate endDate = bookingRequest.getEndTime().toLocalDate();

        Booking booking = bookingService.createBooking(stationId, startDate, endDate, userId);

        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, updatedBooking));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<Booking> confirmBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.confirmBooking(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
=======
//    @PostMapping
//    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
//        try {
//            Integer userId = bookingRequest.getUserId();
//            Integer stationId = bookingRequest.getStationId();
//            LocalDateTime startTime = bookingRequest.getStartTime();
//            LocalDateTime endTime = bookingRequest.getEndTime();
//
//            Booking response = bookingService.createBooking(
//                    stationId, startTime, endTime, userId);
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (RuntimeException ex) {
//            // return meaningful client error for known runtime issues
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
//        } catch (Exception ex) {
//            // generic fallback to avoid exposing internals
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal server error"));
//        }
//    }
}
>>>>>>> 913dc038178c8d2e3085d4faba3d7a05e32badbd
