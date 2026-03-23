package com.course.management.controller;

import com.course.management.common.Result;
import com.course.management.dto.BookingRequest;
import com.course.management.dto.PurchaseRequest;
import com.course.management.entity.Booking;
import com.course.management.entity.Course;
import com.course.management.entity.User;
import com.course.management.mapper.UserMapper;
import com.course.management.service.BookingService;
import com.course.management.service.CourseService;
import com.course.management.service.HoursService;
import com.course.management.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private HoursService hoursService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserMapper userMapper;

    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        return user.getId();
    }

    @GetMapping("/courses")
    public Result<List<Course>> browseCourses() {
        return Result.success(courseService.getAllActiveCourses());
    }

    @PostMapping("/courses/{courseId}/purchase")
    public Result<Void> purchaseHours(@PathVariable Long courseId,
                                      @Valid @RequestBody PurchaseRequest request,
                                      Authentication authentication) {
        try {
            Long studentId = getCurrentUserId(authentication);
            hoursService.purchaseHours(studentId, courseId, request);
            return Result.success("购买成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/hours")
    public Result<List<Map<String, Object>>> getMyHours(Authentication authentication) {
        Long studentId = getCurrentUserId(authentication);
        return Result.success(hoursService.getStudentHours(studentId));
    }

    @GetMapping("/schedules")
    public Result<List<Map<String, Object>>> getAvailableSchedules() {
        return Result.success(scheduleService.getAvailableSchedules());
    }

    @PostMapping("/bookings")
    public Result<Booking> createBooking(@Valid @RequestBody BookingRequest request,
                                         Authentication authentication) {
        try {
            Long studentId = getCurrentUserId(authentication);
            return Result.success(bookingService.createBooking(studentId, request));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/bookings")
    public Result<List<Map<String, Object>>> getMyBookings(Authentication authentication) {
        Long studentId = getCurrentUserId(authentication);
        return Result.success(bookingService.getStudentBookings(studentId));
    }

    @PutMapping("/bookings/{id}/cancel")
    public Result<Void> cancelBooking(@PathVariable Long id,
                                      Authentication authentication) {
        try {
            Long studentId = getCurrentUserId(authentication);
            bookingService.cancelBooking(studentId, id);
            return Result.success("取消成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
