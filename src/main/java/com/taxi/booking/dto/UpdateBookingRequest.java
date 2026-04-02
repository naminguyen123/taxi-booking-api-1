package com.taxi.booking.dto;

import java.math.BigDecimal;

public class UpdateBookingRequest {
    public String pickupLocation;
    public String dropoffLocation;
    public String status;
    public BigDecimal totalFare;
}