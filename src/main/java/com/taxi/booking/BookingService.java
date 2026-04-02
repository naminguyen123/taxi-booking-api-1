package com.taxi.booking;

import com.taxi.booking.dto.CreateBookingRequest;
import com.taxi.booking.dto.UpdateBookingRequest;
import com.taxi.booking.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(String authHeader, CreateBookingRequest request) {
        Long userId = getUserIdFromHeader(authHeader);

        Booking booking = new Booking();
        booking.setPassengerId(userId);
        booking.setPickupLocation(request.pickupLocation);
        booking.setDropoffLocation(request.dropoffLocation);
        booking.setTotalFare(request.totalFare);
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings(String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        return bookingRepository.findByPassengerId(userId);
    }

    public Booking getBookingDetail(String authHeader, Long bookingId) {
        Long userId = getUserIdFromHeader(authHeader);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking"));

        if (!booking.getPassengerId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền xem booking này");
        }

        return booking;
    }

    public Booking updateBooking(String authHeader, Long bookingId, UpdateBookingRequest request) {
        Long userId = getUserIdFromHeader(authHeader);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy booking"));

        if (!booking.getPassengerId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền sửa booking này");
        }

        if (request.pickupLocation != null) booking.setPickupLocation(request.pickupLocation);
        if (request.dropoffLocation != null) booking.setDropoffLocation(request.dropoffLocation);
        if (request.status != null) booking.setStatus(request.status);
        if (request.totalFare != null) booking.setTotalFare(request.totalFare);

        return bookingRepository.save(booking);
    }

    private Long getUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token không hợp lệ");
        }

        String token = authHeader.substring(7);
        return JwtUtil.getUserIdFromToken(token);
    }
}