package com.taxi.booking.dto;

import java.math.BigDecimal;

public class CreateBookingRequest {
    public String pickupLocation;
    public String dropoffLocation;
    public BigDecimal totalFare;
}