package com.course.management.controller;

import com.course.management.common.Result;
import com.course.management.dto.CourseDTO;
import com.course.management.dto.ScheduleDTO;
import com.course.management.entity.Course;
import com.course.management.entity.CourseSchedule;
import com.course.management.entity.User;
import com.course.management.mapper.UserMapper;
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
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private HoursService hoursService;

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

    // Course endpoints
    @GetMapping("/courses")
    public Result<List<Course>> getCourses(Authentication authentication) {
        Long teacherId = getCurrentUserId(authentication);
        return Result.success(courseService.getTeacherCourses(teacherId));
    }

    @PostMapping("/courses")
    public Result<Course> createCourse(@Valid @RequestBody CourseDTO dto,
                                       Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            return Result.success(courseService.createCourse(teacherId, dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/courses/{id}")
    public Result<Course> updateCourse(@PathVariable Long id,
                                       @Valid @RequestBody CourseDTO dto,
                                       Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            return Result.success(courseService.updateCourse(teacherId, id, dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/courses/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id,
                                     Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            courseService.deleteCourse(teacherId, id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // Schedule endpoints
    @GetMapping("/schedules")
    public Result<List<Map<String, Object>>> getSchedules(Authentication authentication) {
        Long teacherId = getCurrentUserId(authentication);
        return Result.success(scheduleService.getTeacherSchedules(teacherId));
    }

    @PostMapping("/schedules")
    public Result<CourseSchedule> createSchedule(@Valid @RequestBody ScheduleDTO dto,
                                                  Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            return Result.success(scheduleService.createSchedule(teacherId, dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/schedules/{id}")
    public Result<CourseSchedule> updateSchedule(@PathVariable Long id,
                                                  @Valid @RequestBody ScheduleDTO dto,
                                                  Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            return Result.success(scheduleService.updateSchedule(teacherId, id, dto));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/schedules/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id,
                                       Authentication authentication) {
        try {
            Long teacherId = getCurrentUserId(authentication);
            scheduleService.deleteSchedule(teacherId, id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // Students enrolled in teacher's courses
    @GetMapping("/students")
    public Result<List<Map<String, Object>>> getStudents(Authentication authentication) {
        Long teacherId = getCurrentUserId(authentication);
        return Result.success(hoursService.getStudentHoursByTeacher(teacherId));
    }
}
