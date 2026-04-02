package com.taxi.booking.controller;

import com.taxi.booking.Booking;
import com.taxi.booking.BookingService;
import com.taxi.booking.dto.CreateBookingRequest;
import com.taxi.booking.dto.UpdateBookingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateBookingRequest request
    ) {
        try {
            Booking booking = bookingService.createBooking(authHeader, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyBookings(@RequestHeader("Authorization") String authHeader) {
        try {
            return ResponseEntity.ok(bookingService.getMyBookings(authHeader));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(bookingService.getBookingDetail(authHeader, id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody UpdateBookingRequest request
    ) {
        try {
            return ResponseEntity.ok(bookingService.updateBooking(authHeader, id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}