package com.course.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.management.entity.StudentCourseHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentCourseHoursMapper extends BaseMapper<StudentCourseHours> {

    @Select("SELECT sch.*, c.name as course_name, c.price_per_hour, u.real_name as teacher_name " +
            "FROM student_course_hours sch " +
            "JOIN courses c ON sch.course_id = c.id " +
            "JOIN users u ON c.teacher_id = u.id " +
            "WHERE sch.student_id = #{studentId}")
    List<java.util.Map<String, Object>> findHoursWithCourseInfo(Long studentId);

    @Select("SELECT sch.*, u.username as student_name, u.real_name, c.name as course_name " +
            "FROM student_course_hours sch " +
            "JOIN users u ON sch.student_id = u.id " +
            "JOIN courses c ON sch.course_id = c.id " +
            "WHERE c.teacher_id = #{teacherId}")
    List<java.util.Map<String, Object>> findStudentHoursByTeacher(Long teacherId);
}
