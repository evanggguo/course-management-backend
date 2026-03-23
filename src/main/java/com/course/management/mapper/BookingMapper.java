package com.course.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.management.entity.Booking;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookingMapper extends BaseMapper<Booking> {

    @Select("SELECT b.*, cs.start_time, cs.end_time, c.name as course_name, " +
            "u.real_name as teacher_name " +
            "FROM bookings b " +
            "JOIN course_schedules cs ON b.schedule_id = cs.id " +
            "JOIN courses c ON b.course_id = c.id " +
            "JOIN users u ON c.teacher_id = u.id " +
            "WHERE b.student_id = #{studentId} " +
            "ORDER BY b.created_at DESC")
    List<Map<String, Object>> findBookingsWithDetails(Long studentId);
}
