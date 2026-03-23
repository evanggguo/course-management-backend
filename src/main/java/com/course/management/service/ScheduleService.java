package com.course.management.service;

import com.course.management.dto.ScheduleDTO;
import com.course.management.entity.CourseSchedule;

import java.util.List;
import java.util.Map;

public interface ScheduleService {

    List<Map<String, Object>> getTeacherSchedules(Long teacherId);

    CourseSchedule createSchedule(Long teacherId, ScheduleDTO dto);

    CourseSchedule updateSchedule(Long teacherId, Long scheduleId, ScheduleDTO dto);

    void deleteSchedule(Long teacherId, Long scheduleId);

    List<Map<String, Object>> getAvailableSchedules();
}
