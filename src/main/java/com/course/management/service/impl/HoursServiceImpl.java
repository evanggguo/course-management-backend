package com.course.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.course.management.dto.PurchaseRequest;
import com.course.management.entity.Course;
import com.course.management.entity.HourPurchaseRecord;
import com.course.management.entity.StudentCourseHours;
import com.course.management.mapper.CourseMapper;
import com.course.management.mapper.HourPurchaseRecordMapper;
import com.course.management.mapper.StudentCourseHoursMapper;
import com.course.management.service.HoursService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class HoursServiceImpl implements HoursService {

    @Autowired
    private StudentCourseHoursMapper hoursMapper;

    @Autowired
    private HourPurchaseRecordMapper purchaseRecordMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    @Transactional
    public void purchaseHours(Long studentId, Long courseId, PurchaseRequest request) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (course.getStatus() != Course.Status.ACTIVE) {
            throw new RuntimeException("课程已停止招生");
        }

        // Find or create student_course_hours record
        StudentCourseHours hours = hoursMapper.selectOne(
                new LambdaQueryWrapper<StudentCourseHours>()
                        .eq(StudentCourseHours::getStudentId, studentId)
                        .eq(StudentCourseHours::getCourseId, courseId));

        if (hours == null) {
            hours = new StudentCourseHours();
            hours.setStudentId(studentId);
            hours.setCourseId(courseId);
            hours.setTotalHours(request.getHours());
            hours.setUsedHours(0);
            hours.setRemainingHours(request.getHours());
            hoursMapper.insert(hours);
        } else {
            hours.setTotalHours(hours.getTotalHours() + request.getHours());
            hours.setRemainingHours(hours.getRemainingHours() + request.getHours());
            hoursMapper.updateById(hours);
        }

        // Create purchase record
        BigDecimal amount = course.getPricePerHour().multiply(
                BigDecimal.valueOf(request.getHours()));
        HourPurchaseRecord record = new HourPurchaseRecord();
        record.setStudentId(studentId);
        record.setCourseId(courseId);
        record.setHoursPurchased(request.getHours());
        record.setAmount(amount);
        purchaseRecordMapper.insert(record);
    }

    @Override
    public List<Map<String, Object>> getStudentHours(Long studentId) {
        return hoursMapper.findHoursWithCourseInfo(studentId);
    }

    @Override
    public List<Map<String, Object>> getStudentHoursByTeacher(Long teacherId) {
        return hoursMapper.findStudentHoursByTeacher(teacherId);
    }

    @Override
    public boolean hasEnoughHours(Long studentId, Long courseId) {
        StudentCourseHours hours = hoursMapper.selectOne(
                new LambdaQueryWrapper<StudentCourseHours>()
                        .eq(StudentCourseHours::getStudentId, studentId)
                        .eq(StudentCourseHours::getCourseId, courseId));
        return hours != null && hours.getRemainingHours() > 0;
    }

    @Override
    @Transactional
    public void deductHour(Long studentId, Long courseId) {
        StudentCourseHours hours = hoursMapper.selectOne(
                new LambdaQueryWrapper<StudentCourseHours>()
                        .eq(StudentCourseHours::getStudentId, studentId)
                        .eq(StudentCourseHours::getCourseId, courseId));
        if (hours == null || hours.getRemainingHours() <= 0) {
            throw new RuntimeException("课时不足");
        }
        hours.setUsedHours(hours.getUsedHours() + 1);
        hours.setRemainingHours(hours.getRemainingHours() - 1);
        hoursMapper.updateById(hours);
    }

    @Override
    @Transactional
    public void refundHour(Long studentId, Long courseId) {
        StudentCourseHours hours = hoursMapper.selectOne(
                new LambdaQueryWrapper<StudentCourseHours>()
                        .eq(StudentCourseHours::getStudentId, studentId)
                        .eq(StudentCourseHours::getCourseId, courseId));
        if (hours != null && hours.getUsedHours() > 0) {
            hours.setUsedHours(hours.getUsedHours() - 1);
            hours.setRemainingHours(hours.getRemainingHours() + 1);
            hoursMapper.updateById(hours);
        }
    }
}
