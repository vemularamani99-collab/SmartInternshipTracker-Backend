package com.smartinternship.backend.service.impl;

import com.smartinternship.backend.dto.AuthResponse;
import com.smartinternship.backend.dto.LoginRequest;
import com.smartinternship.backend.dto.RegisterRequest;
import com.smartinternship.backend.dto.UserResponse;
import com.smartinternship.backend.exception.BadRequestException;
import com.smartinternship.backend.exception.ResourceNotFoundException;
import com.smartinternship.backend.model.Role;
import com.smartinternship.backend.model.User;
import com.smartinternship.backend.repository.UserRepository;
import com.smartinternship.backend.security.JwtUtils;
import com.smartinternship.backend.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // 1. Ensure email uniqueness
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("An account with email '" + normalizedEmail + "' already exists.");
        }

        // 2. Instantiate user entity
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);

        // 3. SECURITY: Hash password using BCrypt before storing in database
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 4. SECURITY RULE: Public registration is strictly restricted to Role.STUDENT
        // Users can never register themselves as ADMIN
        user.setRole(Role.STUDENT);

        user.setCollege(request.getCollege());
        user.setDegree(request.getDegree());
        user.setGraduationYear(request.getGraduationYear());

        User savedUser = userRepository.save(user);

        // 5. Generate signed JWT token
        String token = jwtUtils.generateToken(savedUser);

        // 6. Return response with sanitized User DTO (never exposing password hash)
        return new AuthResponse(
            token,
            jwtUtils.getExpirationMs(),
            mapToUserResponse(savedUser)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        // 1. Lookup user by email
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Invalid email or password."));

        // 2. Verify password using BCrypt matches check
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password.");
        }

        // 3. Generate signed JWT token with claims
        String token = jwtUtils.generateToken(user);

        // 4. Return authentication response
        return new AuthResponse(
            token,
            jwtUtils.getExpirationMs(),
            mapToUserResponse(user)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToUserResponse(user);
    }

    // --- Sanitized Mapper (Excludes Password Hash) ---
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
