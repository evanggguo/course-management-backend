package com.course.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.course.management.dto.CourseDTO;
import com.course.management.entity.Course;
import com.course.management.mapper.CourseMapper;
import com.course.management.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> getTeacherCourses(Long teacherId) {
        return courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getTeacherId, teacherId)
                        .orderByDesc(Course::getCreatedAt));
    }

    @Override
    public Course createCourse(Long teacherId, CourseDTO dto) {
        Course course = new Course();
        course.setTeacherId(teacherId);
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setPricePerHour(dto.getPricePerHour());
        course.setStatus(dto.getStatus() != null ? dto.getStatus() : Course.Status.ACTIVE);
        courseMapper.insert(course);
        return course;
    }

    @Override
    public Course updateCourse(Long teacherId, Long courseId, CourseDTO dto) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限修改此课程");
        }
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setPricePerHour(dto.getPricePerHour());
        if (dto.getStatus() != null) {
            course.setStatus(dto.getStatus());
        }
        courseMapper.updateById(course);
        return course;
    }

    @Override
    public void deleteCourse(Long teacherId, Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限删除此课程");
        }
        courseMapper.deleteById(courseId);
    }

    @Override
    public List<Course> getAllActiveCourses() {
        return courseMapper.selectList(
                new LambdaQueryWrapper<Course>()
                        .eq(Course::getStatus, Course.Status.ACTIVE)
                        .orderByDesc(Course::getCreatedAt));
    }
}
