package com.smartinternship.backend.service.impl;

import com.smartinternship.backend.dto.UserResponse;
import com.smartinternship.backend.exception.ResourceNotFoundException;
import com.smartinternship.backend.model.Role;
import com.smartinternship.backend.model.User;
import com.smartinternship.backend.repository.UserRepository;
import com.smartinternship.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getAllUsers(Role roleFilter) {
        List<User> users = (roleFilter != null)
                ? userRepository.findByRole(roleFilter)
                : userRepository.findAll();

        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.getCollege(),
            user.getDegree(),
            user.getGraduationYear(),
            user.getCreatedAt()
        );
    }
}
