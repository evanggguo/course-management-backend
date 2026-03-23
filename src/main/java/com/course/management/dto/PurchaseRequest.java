package com.course.management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseRequest {

    @NotNull(message = "购买课时数不能为空")
    @Min(value = 1, message = "购买课时数至少为1")
    private Integer hours;
}
