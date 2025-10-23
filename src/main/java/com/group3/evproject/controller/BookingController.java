package com.group3.evproject.controller;

import com.group3.evproject.dto.request.BookingRequest;
import com.group3.evproject.dto.response.BookingResponse;
import com.group3.evproject.entity.Booking;
import com.group3.evproject.entity.User;
import com.group3.evproject.service.AuthenticationService;
import com.group3.evproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final AuthenticationService authenticationService;

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
            @RequestBody BookingRequest bookingRequest,
            @RequestHeader("Authorization") String accessToken
    ) {
        // lay user tu token
        User user = authenticationService.getUserFromRequest(accessToken);

        //du lieu dau vao
        Long stationId = bookingRequest.getStationId().longValue();
        LocalDateTime timeToCharge = bookingRequest.getTimeToCharge();
        LocalDateTime endTime = bookingRequest.getEndTime();

        //bookingService xu ly booking
        Booking booking = bookingService.createBooking(stationId, timeToCharge, endTime, user.getId());
        BookingResponse response = BookingResponse.builder()
                .bookingId(booking.getId())
                .stationName(booking.getStation().getName())
                .bookingTime(booking.getBookingTime())
                .timeToCharge(booking.getTimeToCharge())
                .endTime(booking.getEndTime())
                .status(booking.getStatus().name())
                .build();
        return ResponseEntity.ok(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, updatedBooking));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Booking> startCharging(
            @PathVariable Long id,
            @RequestParam Long spotId
    ){
        return ResponseEntity.ok(bookingService.startCharging(id, spotId));
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

