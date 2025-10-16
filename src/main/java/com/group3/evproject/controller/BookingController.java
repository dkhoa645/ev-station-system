package com.group3.evproject.controller;
import com.group3.evproject.dto.request.BookingRequest;
import com.group3.evproject.entity.Booking;
import com.group3.evproject.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


@RestController
@RequestMapping ("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    //tao mot booking moi
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest bookingRequest){
        Integer userId = bookingRequest.getUserId();
    //    Integer vehicleId = bookingRequest.getVehicleId();
        Integer stationId = bookingRequest.getStationId();
        LocalDateTime startTime = bookingRequest.getStartTime();
        LocalDateTime endTime = bookingRequest.getEndTime();

        Booking response = bookingService.createBooking(
                stationId, startTime, endTime, userId);
        System.out.println("Booking created" + response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//   //lay tat ca booking
//    @GetMapping
//    public ResponseEntity<Booking> getAllBookings(@PathVariable Integer id){
//        Booking booking = bookingService.getBookingById(id);
//        return ResponseEntity.ok((Booking) bookingService.getAllBookings());
//    }
//
//    //lay booking theo id
//    @GetMapping("/{id}")
//    public ResponseEntity<Booking> getBookingById(@PathVariable Integer id) {
//        Booking booking = bookingService.getBookingById(id);
//        return ResponseEntity.ok(booking);
//    }
//
//    //lay tat ca booking cua 1 user
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<Booking> getBookingsByUserId(@PathVariable Integer userId) {
//        return ResponseEntity.ok((Booking) bookingService.getBookingsByUserId(userId));
//    }
//
//    //lay tat ca booking cua 1 station
//    @GetMapping("/station/{stationId}")
//    public List<Booking> getBookingsByStationId(@PathVariable Integer stationId) {
//        return bookingService.getBookingsByStationId(stationId);
//    }
//
//    //lay tat ca booking theo trang thai
//    @GetMapping("/status/{status}")
//    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String status) {
//        List<Booking> bookings = bookingService.getBookingsByStatus(status);
//        return ResponseEntity.ok(bookings);
//    }
//
//    //lay tat ca booking trong thoi gian
//    @GetMapping("/range")
//    public ResponseEntity<List<Booking>> getBookingsBetween(
//            @RequestParam("start") LocalDateTime start,
//            @RequestParam("end") LocalDateTime end) {
//        List<Booking> bookings = bookingService.getBookingBetween(start, end);
//        return ResponseEntity.ok(bookings);
//    }
//
//    //update booking
//    @PutMapping("/{id}")
//    public ResponseEntity<Booking> updateBooking(@PathVariable Integer id, @RequestBody Booking updatedBooking) {
//        Booking updated = bookingService.updateBooking(id, updatedBooking);
//        return ResponseEntity.ok(updated);
//    }
//
//    //cancel booking
//    @PutMapping("/{id}/cancel")
//    public ResponseEntity<Void> cancelBooking(@PathVariable Integer id) {
//        bookingService.cancelBooking(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    //check user's booking status
//    @GetMapping("/user/{userId}/status")
//    public ResponseEntity<Boolean> checkUserBookingStatus(@PathVariable Integer userId) {
//        boolean hasActive = bookingService.checkActiveBooking(userId);
//        return ResponseEntity.ok(hasActive);
//    }
}
