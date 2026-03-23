package com.course.management.service;

import com.course.management.dto.BookingRequest;
import com.course.management.entity.Booking;

import java.util.List;
import java.util.Map;

public interface BookingService {

    Booking createBooking(Long studentId, BookingRequest request);

    List<Map<String, Object>> getStudentBookings(Long studentId);

    void cancelBooking(Long studentId, Long bookingId);
}
