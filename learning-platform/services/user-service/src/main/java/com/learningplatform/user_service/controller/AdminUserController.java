package com.learningplatform.user_service.controller;

import com.learningplatform.user_service.dto.UpdateUserRequest;
import com.learningplatform.user_service.dto.UserResponse;
import com.learningplatform.user_service.entity.User;
import com.learningplatform.user_service.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(
            UserService userService) {

        this.userService = userService;
    }

    /*
     * GET ALL USERS
     *
     * ADMIN ONLY
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users =
                userService.getAllUsers()
                        .stream()
                        .map(this::toUserResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    /*
     * GET USER BY ID
     *
     * ADMIN ONLY
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id)
                .map(this::toUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * UPDATE USER
     *
     * ADMIN ONLY
     *
     * Existing UserService authorization is reused.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        User updatedUser =
                userService.updateUser(id, request);

        return ResponseEntity.ok(
                toUserResponse(updatedUser));
    }

    /*
     * DELETE USER
     *
     * ADMIN ONLY
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    private UserResponse toUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}