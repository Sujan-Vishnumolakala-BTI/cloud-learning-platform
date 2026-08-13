package com.learningplatform.user_service.service;

import com.learningplatform.user_service.dto.SkillRequest;
import com.learningplatform.user_service.dto.UpdateUserRequest;
import com.learningplatform.user_service.entity.User;
import com.learningplatform.user_service.exception.AccessDeniedException;
import com.learningplatform.user_service.exception.EmailAlreadyExistsException;
import com.learningplatform.user_service.exception.RoleChangeNotAllowedException;
import com.learningplatform.user_service.exception.UserNotFoundException;
import com.learningplatform.user_service.repository.UserRepository;
import com.learningplatform.user_service.entity.UserSkill;
import com.learningplatform.user_service.repository.UserSkillRepository;

// import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import com.learningplatform.user_service.repository.RefreshTokenRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final RefreshTokenRepository refreshTokenRepository;
        private final UserSkillRepository userSkillRepository;

        public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                        RefreshTokenRepository refreshTokenRepository, UserSkillRepository userSkillRepository) {
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.refreshTokenRepository = refreshTokenRepository;
                this.userSkillRepository = userSkillRepository;
        }

        // public User createUser(User user) {

        // if (userRepository.existsByEmail(user.getEmail())) {
        // throw new EmailAlreadyExistsException("Email already registered");
        // }

        // user.setPassword(passwordEncoder.encode(user.getPassword()));

        // return userRepository.save(user);
        // }

        public User createUser(User user) {

                if (userRepository.existsByEmail(user.getEmail())) {
                        throw new EmailAlreadyExistsException("Email already registered");
                }

                user.setPassword(passwordEncoder.encode(user.getPassword()));

                User savedUser = userRepository.save(user);

                return savedUser;
        }

        // public List<User> getAllUsers() {
        // return userRepository.findAll();
        // }

        public User getCurrentUser() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !authentication.isAuthenticated()) {

                        throw new UserNotFoundException(
                                        "Authenticated user not found");
                }

                String email = authentication.getName();

                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new UserNotFoundException(
                                                "User not found"));
        }

        public List<User> getAllUsers() {

                List<User> users = userRepository.findAll();

                return users;
        }

        // public Optional<User> getUserById(Long id) {
        // return userRepository.findById(id);
        // }

        public Optional<User> getUserById(Long id) {

                Optional<User> user = userRepository.findById(id);

                return user;
        }

        public Optional<User> getUserByEmail(String email) {
                return userRepository.findByEmail(email);
        }

        // public User updateUser(Long id, UpdateUserRequest updatedUser) {

        // User existingUser = userRepository.findById(id)
        // .orElseThrow(() -> new UserNotFoundException("User not found"));

        // if (!isAdmin() && !isCurrentUser(id)) {

        // throw new AccessDeniedException(
        // "You can only update your own account");
        // }
        // existingUser.setFirstName(updatedUser.getFirstName());
        // existingUser.setLastName(updatedUser.getLastName());
        // existingUser.setEmail(updatedUser.getEmail());

        // // Role can only be changed by ADMIN
        // if (updatedUser.getRole() != null
        // && updatedUser.getRole() != existingUser.getRole()) {

        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();

        // // boolean isAdmin = authentication.getAuthorities()
        // // .stream()
        // // .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // boolean isAdmin = authentication.getAuthorities()
        // .stream()
        // .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // if (!isAdmin) {
        // throw new RoleChangeNotAllowedException(
        // "Only ADMIN can change user roles");
        // }

        // existingUser.setRole(updatedUser.getRole());
        // }

        // // Update password only when supplied
        // if (updatedUser.getPassword() != null
        // && !updatedUser.getPassword().isBlank()) {

        // existingUser.setPassword(
        // passwordEncoder.encode(updatedUser.getPassword()));
        // }

        // return userRepository.save(existingUser);
        // }

        public User updateUser(Long id, UpdateUserRequest updatedUser) {

                User existingUser = userRepository.findById(id)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                // if (!isAdmin()) {
                // throw new RoleChangeNotAllowedException(
                // "Only ADMIN can change user roles");
                // }

                // ADMIN can update anyone.
                // Normal user can update only their own account.
                if (!isAdmin() && !isCurrentUser(id)) {

                        throw new AccessDeniedException(
                                        "You can only update your own account");
                }

                // Update first name only if supplied
                if (updatedUser.getFirstName() != null
                                && !updatedUser.getFirstName().isBlank()) {

                        existingUser.setFirstName(
                                        updatedUser.getFirstName());
                }

                // Update last name only if supplied
                if (updatedUser.getLastName() != null
                                && !updatedUser.getLastName().isBlank()) {

                        existingUser.setLastName(
                                        updatedUser.getLastName());
                }

                // Update email only if supplied
                if (updatedUser.getEmail() != null
                                && !updatedUser.getEmail().isBlank()) {

                        // Optional: prevent duplicate email
                        if (!updatedUser.getEmail()
                                        .equals(existingUser.getEmail())
                                        && userRepository.existsByEmail(
                                                        updatedUser.getEmail())) {

                                throw new EmailAlreadyExistsException(
                                                "Email already registered");
                        }

                        existingUser.setEmail(
                                        updatedUser.getEmail());
                }

                // Role can ONLY be changed by ADMIN
                if (updatedUser.getRole() != null
                                && updatedUser.getRole() != existingUser.getRole()) {

                        if (!isAdmin()) {

                                throw new RoleChangeNotAllowedException(
                                                "Only ADMIN can change user roles");
                        }

                        existingUser.setRole(
                                        updatedUser.getRole());
                }

                // Password update only when supplied
                if (updatedUser.getPassword() != null
                                && !updatedUser.getPassword().isBlank()) {

                        existingUser.setPassword(
                                        passwordEncoder.encode(
                                                        updatedUser.getPassword()));
                }

                // Enable / disable account
                // Only ADMIN can change account status
                if (updatedUser.getEnabled() != null) {

                        if (!isAdmin()) {
                                throw new AccessDeniedException(
                                                "Only ADMIN can enable or disable accounts");
                        }

                        existingUser.setEnabled(
                                        updatedUser.getEnabled());
                }

                return userRepository.save(existingUser);
        }

        // public void deleteUser(Long id) {
        // if (!userRepository.existsById(id)) {

        // throw new UserNotFoundException("User not found");
        // }

        // if (!isAdmin() && !isCurrentUser(id)) {

        // throw new AccessDeniedException(
        // "You can only delete your own account");
        // }

        // userRepository.deleteById(id);
        // }

        @Transactional
        public void deleteUser(Long id) {

                if (!userRepository.existsById(id)) {

                        throw new UserNotFoundException(
                                        "User not found");
                }

                if (!isAdmin() && !isCurrentUser(id)) {

                        throw new AccessDeniedException(
                                        "You can only delete your own account");
                }

                /*
                 * Delete all refresh tokens first.
                 * They have a foreign-key reference to users.id.
                 */
                refreshTokenRepository.deleteByUserId(id);

                /*
                 * Now delete the user.
                 */
                userRepository.deleteById(id);
        }

        private boolean isAdmin() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                return authentication != null
                                && authentication.getAuthorities()
                                                .stream()
                                                .anyMatch(authority -> authority.getAuthority()
                                                                .equals("ROLE_ADMIN"));
        }

        private boolean isCurrentUser(Long userId) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null) {
                        return false;
                }

                String currentEmail = authentication.getName();

                return userRepository.findById(userId)
                                .map(user -> user.getEmail()
                                                .equals(currentEmail))
                                .orElse(false);
        }

        @Transactional
        public List<UserSkill> getMySkills() {

                User user = getCurrentUser();

                return userSkillRepository.findByUserId(user.getId());
        }

        @Transactional
        public List<UserSkill> saveMySkills(
                        List<SkillRequest> skills) {

                User user = getCurrentUser();

                userSkillRepository.deleteByUserId(
                                user.getId());

                List<UserSkill> userSkills = skills.stream()

                                .filter(skill -> skill.getSkill() != null
                                                && !skill.getSkill().isBlank())

                                .map(skill -> {

                                        UserSkill userSkill = new UserSkill();

                                        userSkill.setUserId(
                                                        user.getId());

                                        userSkill.setSkill(
                                                        skill.getSkill().trim());

                                        userSkill.setProficiency(
                                                        skill.getProficiency());

                                        return userSkill;
                                })

                                .toList();

                userSkillRepository.saveAll(
                                userSkills);

                return userSkills;
        }
}