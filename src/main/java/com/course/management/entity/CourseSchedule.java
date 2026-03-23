package com.course.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course_schedules")
public class CourseSchedule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private Long teacherId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Status status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    public enum Status {
        AVAILABLE, BOOKED, COMPLETED, CANCELLED
    }
}
