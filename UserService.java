package com.enterprise.usermanagement.service;

import com.enterprise.usermanagement.dto.UserRequest;
import com.enterprise.usermanagement.dto.UserResponse;
import com.enterprise.usermanagement.entity.Role;
import com.enterprise.usermanagement.entity.User;
import com.enterprise.usermanagement.exception.DuplicateResourceException;
import com.enterprise.usermanagement.exception.ResourceNotFoundException;
import com.enterprise.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.debug("Fetching user by id: {}", id);
        return UserResponse.fromEntity(findUserById(id));
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Fetching current user profile: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Creating user: {}", request.getUsername());
        validateUniqueFields(null, request.getUsername(), request.getEmail());

        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password is required when creating a user");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                .build();

        User saved = userRepository.save(user);
        log.info("User created with id: {}", saved.getId());
        return UserResponse.fromEntity(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        log.info("Updating user id: {}", id);
        User user = findUserById(id);
        validateUniqueFields(id, request.getUsername(), request.getEmail());

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);
        log.info("User updated successfully id: {}", updated.getId());
        return UserResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user id: {}", id);
        User user = findUserById(id);
        userRepository.delete(user);
        log.info("User deleted id: {}", id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void validateUniqueFields(Long id, String username, String email) {
        userRepository.findByUsername(username).ifPresent(existing -> {
            if (id == null || !existing.getId().equals(id)) {
                throw new DuplicateResourceException("Username already exists: " + username);
            }
        });
        userRepository.findByEmail(email).ifPresent(existing -> {
            if (id == null || !existing.getId().equals(id)) {
                throw new DuplicateResourceException("Email already exists: " + email);
            }
        });
    }
}
