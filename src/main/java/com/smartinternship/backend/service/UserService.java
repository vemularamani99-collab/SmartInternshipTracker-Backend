package com.smartinternship.backend.service;

import com.smartinternship.backend.dto.UserResponse;
import com.smartinternship.backend.model.Role;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers(Role roleFilter);

    UserResponse getUserById(Long id);
}
