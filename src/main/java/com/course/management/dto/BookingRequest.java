package com.course.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull(message = "课程安排ID不能为空")
    private Long scheduleId;
}
