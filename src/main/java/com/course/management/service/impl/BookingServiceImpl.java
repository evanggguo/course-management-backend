package com.course.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.course.management.dto.BookingRequest;
import com.course.management.entity.Booking;
import com.course.management.entity.CourseSchedule;
import com.course.management.mapper.BookingMapper;
import com.course.management.mapper.CourseScheduleMapper;
import com.course.management.service.BookingService;
import com.course.management.service.HoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private CourseScheduleMapper scheduleMapper;

    @Autowired
    private HoursService hoursService;

    @Override
    @Transactional
    public Booking createBooking(Long studentId, BookingRequest request) {
        CourseSchedule schedule = scheduleMapper.selectById(request.getScheduleId());
        if (schedule == null) {
            throw new RuntimeException("课程安排不存在");
        }
        if (schedule.getStatus() != CourseSchedule.Status.AVAILABLE) {
            throw new RuntimeException("该时间段已被预约或不可用");
        }

        // Check if student already booked this schedule
        Long existingCount = bookingMapper.selectCount(
                new LambdaQueryWrapper<Booking>()
                        .eq(Booking::getStudentId, studentId)
                        .eq(Booking::getScheduleId, request.getScheduleId())
                        .eq(Booking::getStatus, Booking.Status.CONFIRMED));
        if (existingCount > 0) {
            throw new RuntimeException("您已预约过该课程安排");
        }

        // Check hours
        if (!hoursService.hasEnoughHours(studentId, schedule.getCourseId())) {
            throw new RuntimeException("该课程课时不足，请先购买课时");
        }

        // Deduct hour
        hoursService.deductHour(studentId, schedule.getCourseId());

        // Update schedule status
        schedule.setStatus(CourseSchedule.Status.BOOKED);
        scheduleMapper.updateById(schedule);

        // Create booking
        Booking booking = new Booking();
        booking.setStudentId(studentId);
        booking.setScheduleId(request.getScheduleId());
        booking.setCourseId(schedule.getCourseId());
        booking.setStatus(Booking.Status.CONFIRMED);
        bookingMapper.insert(booking);

        return booking;
    }

    @Override
    public List<Map<String, Object>> getStudentBookings(Long studentId) {
        return bookingMapper.findBookingsWithDetails(studentId);
    }

    @Override
    @Transactional
    public void cancelBooking(Long studentId, Long bookingId) {
        Booking booking = bookingMapper.selectById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!booking.getStudentId().equals(studentId)) {
            throw new RuntimeException("无权限取消此预约");
        }
        if (booking.getStatus() != Booking.Status.CONFIRMED) {
            throw new RuntimeException("该预约无法取消");
        }

        // Update booking status
        booking.setStatus(Booking.Status.CANCELLED);
        bookingMapper.updateById(booking);

        // Restore schedule status
        CourseSchedule schedule = scheduleMapper.selectById(booking.getScheduleId());
        if (schedule != null) {
            schedule.setStatus(CourseSchedule.Status.AVAILABLE);
            scheduleMapper.updateById(schedule);
        }

        // Refund hour
        hoursService.refundHour(studentId, booking.getCourseId());
    }
}
