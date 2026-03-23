package com.course.management.service;

import com.course.management.dto.CourseDTO;
import com.course.management.entity.Course;

import java.util.List;

public interface CourseService {

    List<Course> getTeacherCourses(Long teacherId);

    Course createCourse(Long teacherId, CourseDTO dto);

    Course updateCourse(Long teacherId, Long courseId, CourseDTO dto);

    void deleteCourse(Long teacherId, Long courseId);

    List<Course> getAllActiveCourses();
}
