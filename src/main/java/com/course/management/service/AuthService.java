package com.course.management.service;

import com.course.management.dto.LoginRequest;
import com.course.management.dto.LoginResponse;
import com.course.management.dto.RegisterRequest;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
