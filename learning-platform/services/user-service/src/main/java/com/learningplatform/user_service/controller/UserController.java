package com.learningplatform.user_service.controller;

import com.learningplatform.user_service.dto.CreateUserRequest;
import com.learningplatform.user_service.dto.SkillResponse;
import com.learningplatform.user_service.dto.UpdateUserRequest;
import com.learningplatform.user_service.dto.UserResponse;
import com.learningplatform.user_service.entity.User;
import com.learningplatform.user_service.entity.UserSkill;
import com.learningplatform.user_service.service.UserService;
import com.learningplatform.user_service.dto.UserSkillRequest;
import com.learningplatform.user_service.dto.UserSkillResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());

        if (request.getRole() != null) {
            user.setRole(
                    request.getRole());
        }

        User createdUser = userService.createUser(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {

        User user = userService.getCurrentUser();

        return ResponseEntity.ok(
                toUserResponse(user));
    }
    // @GetMapping("/{id}")
    // public ResponseEntity<User> getUserById(@PathVariable Long id) {

    // return userService.getUserById(id)
    // .map(ResponseEntity::ok)
    // .orElse(ResponseEntity.notFound().build());
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<User> getUserById(@PathVariable Long id) {

    // return userService.getUserById(id)
    // .map(user -> {

    // return ResponseEntity.ok(user);
    // })
    // .orElseGet(() -> {

    // return ResponseEntity.notFound().build();
    // });
    // }

    // @GetMapping("/{id}")
    // public ResponseEntity<User> getUserById(@PathVariable Long id) {

    // Optional<User> user = userService.getUserById(id);

    // return user
    // .map(ResponseEntity::ok)
    // .orElse(ResponseEntity.notFound().build());
    // }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return userService.getUserById(id)
                .map(this::toUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // @GetMapping("/email/{email}")
    // public ResponseEntity<User> getUserByEmail(@PathVariable String email) {

    // return userService.getUserByEmail(email)
    // .map(ResponseEntity::ok)
    // .orElse(ResponseEntity.notFound().build());
    // }
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @PathVariable String email) {

        return userService.getUserByEmail(email)
                .map(this::toUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest user) {

        User updatedUser = userService.updateUser(id, user);

        return ResponseEntity.ok(updatedUser);
        // } catch (RuntimeException e) {
        // return ResponseEntity.notFound().build();
        // }
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/student-test")
    public String studentTest() {
        return "Student access granted";
    }

    @GetMapping("/instructor-test")
    public String instructorTest() {
        return "Instructor access granted";
    }

    @GetMapping("/admin-test")
    public String adminTest() {
        return "Admin access granted";
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponse>> getAllUsersForAdmin() {

        List<UserResponse> users = userService.getAllUsers()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

   @GetMapping("/me/skills")
public ResponseEntity<UserSkillResponse> getMySkills() {

    User user =
            userService.getCurrentUser();

    List<UserSkill> skills =
            userService.getMySkills();

    List<SkillResponse> response =
            skills.stream()
                    .map(skill ->
                            new SkillResponse(
                                    skill.getSkill(),
                                    skill.getProficiency()
                            )
                    )
                    .toList();

    return ResponseEntity.ok(
            new UserSkillResponse(
                    user.getId(),
                    response
            )
    );
}

@PutMapping("/me/skills")
public ResponseEntity<UserSkillResponse> saveMySkills(
        @Valid @RequestBody UserSkillRequest request) {

    User user =
            userService.getCurrentUser();

    List<UserSkill> skills =
            userService.saveMySkills(
                    request.getSkills()
            );

    List<SkillResponse> response =
            skills.stream()
                    .map(skill ->
                            new SkillResponse(
                                    skill.getSkill(),
                                    skill.getProficiency()
                            )
                    )
                    .toList();

        return ResponseEntity.ok(
                new UserSkillResponse(
                        user.getId(),
                        response
                )
        );
}

}