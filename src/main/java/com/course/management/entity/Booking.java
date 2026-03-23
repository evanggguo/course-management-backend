package com.course.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("bookings")
public class Booking {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long scheduleId;

    private Long courseId;

    private Status status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED
    }
}
