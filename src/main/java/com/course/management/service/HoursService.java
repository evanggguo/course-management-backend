package com.course.management.service;

import com.course.management.dto.PurchaseRequest;

import java.util.List;
import java.util.Map;

public interface HoursService {

    void purchaseHours(Long studentId, Long courseId, PurchaseRequest request);

    List<Map<String, Object>> getStudentHours(Long studentId);

    List<Map<String, Object>> getStudentHoursByTeacher(Long teacherId);

    boolean hasEnoughHours(Long studentId, Long courseId);

    void deductHour(Long studentId, Long courseId);

    void refundHour(Long studentId, Long courseId);
}
