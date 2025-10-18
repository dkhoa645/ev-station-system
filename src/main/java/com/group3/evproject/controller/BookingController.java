// java
package com.group3.evproject.controller;

import com.group3.evproject.dto.request.BookingRequest;
import com.group3.evproject.entity.Booking;
import com.group3.evproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

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