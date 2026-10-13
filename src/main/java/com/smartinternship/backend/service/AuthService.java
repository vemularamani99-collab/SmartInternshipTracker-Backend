package com.smartinternship.backend.service;

import com.smartinternship.backend.dto.AuthResponse;
import com.smartinternship.backend.dto.LoginRequest;
import com.smartinternship.backend.dto.RegisterRequest;
import com.smartinternship.backend.dto.UserResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getCurrentUserProfile(String email);
}
