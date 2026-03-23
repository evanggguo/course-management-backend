package com.course.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.course.management.dto.ScheduleDTO;
import com.course.management.entity.Course;
import com.course.management.entity.CourseSchedule;
import com.course.management.mapper.CourseMapper;
import com.course.management.mapper.CourseScheduleMapper;
import com.course.management.service.ScheduleService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private CourseScheduleMapper scheduleMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Map<String, Object>> getTeacherSchedules(Long teacherId) {
        List<CourseSchedule> schedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>()
                        .eq(CourseSchedule::getTeacherId, teacherId)
                        .orderByDesc(CourseSchedule::getStartTime));

        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseSchedule schedule : schedules) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", schedule.getId());
            item.put("courseId", schedule.getCourseId());
            item.put("teacherId", schedule.getTeacherId());
            item.put("startTime", schedule.getStartTime());
            item.put("endTime", schedule.getEndTime());
            item.put("status", schedule.getStatus());
            item.put("createdAt", schedule.getCreatedAt());

            Course course = courseMapper.selectById(schedule.getCourseId());
            if (course != null) {
                item.put("courseName", course.getName());
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public CourseSchedule createSchedule(Long teacherId, ScheduleDTO dto) {
        Course course = courseMapper.selectById(dto.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        if (!course.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限为此课程创建安排");
        }

        CourseSchedule schedule = new CourseSchedule();
        schedule.setCourseId(dto.getCourseId());
        schedule.setTeacherId(teacherId);
        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        schedule.setStatus(CourseSchedule.Status.AVAILABLE);
        scheduleMapper.insert(schedule);
        return schedule;
    }

    @Override
    public CourseSchedule updateSchedule(Long teacherId, Long scheduleId, ScheduleDTO dto) {
        CourseSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("课程安排不存在");
        }
        if (!schedule.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限修改此课程安排");
        }

        schedule.setStartTime(dto.getStartTime());
        schedule.setEndTime(dto.getEndTime());
        if (dto.getStatus() != null) {
            schedule.setStatus(dto.getStatus());
        }
        scheduleMapper.updateById(schedule);
        return schedule;
    }

    @Override
    public void deleteSchedule(Long teacherId, Long scheduleId) {
        CourseSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("课程安排不存在");
        }
        if (!schedule.getTeacherId().equals(teacherId)) {
            throw new RuntimeException("无权限删除此课程安排");
        }
        scheduleMapper.deleteById(scheduleId);
    }

    @Override
    public List<Map<String, Object>> getAvailableSchedules() {
        List<CourseSchedule> schedules = scheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedule>()
                        .eq(CourseSchedule::getStatus, CourseSchedule.Status.AVAILABLE)
                        .orderByAsc(CourseSchedule::getStartTime));

        List<Map<String, Object>> result = new ArrayList<>();
        for (CourseSchedule schedule : schedules) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", schedule.getId());
            item.put("courseId", schedule.getCourseId());
            item.put("teacherId", schedule.getTeacherId());
            item.put("startTime", schedule.getStartTime());
            item.put("endTime", schedule.getEndTime());
            item.put("status", schedule.getStatus());

            Course course = courseMapper.selectById(schedule.getCourseId());
            if (course != null) {
                item.put("courseName", course.getName());
                item.put("pricePerHour", course.getPricePerHour());
            }
            result.add(item);
        }
        return result;
    }
}
