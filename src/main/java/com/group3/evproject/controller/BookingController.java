package com.group3.evproject.controller;

import com.group3.evproject.dto.request.BookingRequest;
import com.group3.evproject.dto.response.BookingResponse;
import com.group3.evproject.entity.Booking;
import com.group3.evproject.entity.User;
import com.group3.evproject.service.AuthenticationService;
import com.group3.evproject.service.BookingService;
import com.group3.evproject.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest bookingRequest,
            @RequestHeader("Authorization") String accessToken
    ) {
        // lay user tu token
        User user = authenticationService.getUserFromRequest(accessToken);

        //goi service de tao booking
        Booking booking = bookingService.createBooking(bookingRequest.getStationId(), bookingRequest.getTimeToCharge(), bookingRequest.getEndTime(), user.getId(), bookingRequest.getVehicleId());

        //build response tra ve client
        BookingResponse bookingResponse = BookingResponse.builder()
                .bookingId(booking.getId())
                .vehicleId(booking.getVehicle().getId())
                .stationName(booking.getStation().getName())
                .startTime(booking.getStartTime())
                .timeToCharge(booking.getTimeToCharge())
                .endTime(booking.getEndTime())
                .status(booking.getStatus().name())
                .build();
        return ResponseEntity.ok(bookingResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, updatedBooking));
    }

    @Operation(summary = "Bắt đầu quá trình sạc cho booking")
    @PostMapping("/{id}/start")
    public ResponseEntity<String> startCharging(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken
    ) {
        User user = authenticationService.getUserFromRequest(accessToken);
        bookingService.startCharging(id, user.getId());
        return ResponseEntity.ok("Charging started successfully.");
    }

    @Operation(summary = "Kết thúc quá trình sạc cho booking")
    @PostMapping("/{id}/end")
    public ResponseEntity<String> endCharging(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken
    ) {
        User user = authenticationService.getUserFromRequest(accessToken);
        bookingService.endCharging(id);
        return ResponseEntity.ok("Charging ended successfully.");
    }

    @Operation(summary = "Hủy đặt chỗ sạc (cancel booking)")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelBooking(
            @PathVariable Long id,
            @RequestHeader("Authorization") String accessToken
    ) {
        User user = authenticationService.getUserFromRequest(accessToken);
        bookingService.cancelBooking(id);
        return ResponseEntity.ok("Booking cancelled successfully.");
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

