package com.course.management.dto;

import com.course.management.entity.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseDTO {

    private Long id;

    @NotBlank(message = "课程名称不能为空")
    private String name;

    private String description;

    @NotNull(message = "每课时价格不能为空")
    private BigDecimal pricePerHour;

    private Course.Status status;
}
