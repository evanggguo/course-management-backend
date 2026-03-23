package com.course.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.management.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT DISTINCT u.* FROM users u " +
            "JOIN student_course_hours sch ON u.id = sch.student_id " +
            "JOIN courses c ON sch.course_id = c.id " +
            "WHERE c.teacher_id = #{teacherId} AND u.role = 'STUDENT'")
    List<User> findStudentsByTeacherId(Long teacherId);
}
