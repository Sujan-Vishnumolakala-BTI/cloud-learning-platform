package com.learningplatform.course_service.service;

import com.learningplatform.course_service.dto.CreateCourseRequest;
import com.learningplatform.course_service.client.UserServiceClient;
import com.learningplatform.course_service.dto.CourseResponse;
import com.learningplatform.course_service.dto.UpdateCourseRequest;
import com.learningplatform.course_service.dto.UserCoursesResponse;
import com.learningplatform.course_service.dto.UserResponse;
import com.learningplatform.course_service.entity.Course;
import com.learningplatform.course_service.repository.CourseRepository;
import com.learningplatform.course_service.security.AuthenticatedUser;

import jakarta.servlet.http.HttpServletRequest;

import com.learningplatform.course_service.exception.CourseAlreadyPublishedException;
import com.learningplatform.course_service.exception.CourseNotFoundException;
import com.learningplatform.course_service.exception.CourseStateException;
import com.learningplatform.course_service.event.CourseEventProducer;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.learningplatform.course_service.dto.PageResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
public class CourseService {

        private final CourseRepository courseRepository;
        private final UserServiceClient userServiceClient;
        private final HttpServletRequest request;
        private final CourseEventProducer courseEventProducer;

        public CourseService(
                        CourseRepository courseRepository,
                        UserServiceClient userServiceClient,
                        HttpServletRequest request,
                        CourseEventProducer courseEventProducer) {

                this.courseRepository = courseRepository;
                this.userServiceClient = userServiceClient;
                this.request = request;
                this.courseEventProducer = courseEventProducer;
        }

        /*
         * CREATE COURSE
         *
         * Only INSTRUCTOR or ADMIN can create courses.
         *
         * Instructor ID comes from the authenticated user's JWT.
         */
        public CourseResponse createCourse(
                        CreateCourseRequest request) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                boolean isAdmin = hasRole("ROLE_ADMIN");
                boolean isInstructor = hasRole("ROLE_INSTRUCTOR");

                if (!isAdmin && !isInstructor) {
                        throw new AccessDeniedException(
                                        "Only INSTRUCTOR or ADMIN can create courses");
                }

                Course course = new Course();

                course.setTitle(request.getTitle());
                course.setDescription(request.getDescription());
                course.setCategory(request.getCategory());

                if (request.getSkills() != null) {

                        course.setSkills(
                                        request.getSkills()
                                                        .stream()
                                                        .map(String::trim)
                                                        .filter(skill -> !skill.isBlank())
                                                        .collect(java.util.stream.Collectors.toSet()));

                }

                /*
                 * ADMIN can create a course for a specific instructor.
                 *
                 * INSTRUCTOR can only create a course for themselves.
                 *
                 * Since JWT currently contains email and role,
                 * we are using the instructorId supplied in the request
                 * for now.
                 */
                // course.setInstructorId(request.getInstructorId());
                if (isInstructor) {

                        course.setInstructorId(
                                        getCurrentUserId());

                } else {

                        course.setInstructorId(
                                        request.getInstructorId());
                }

                Course savedCourse = courseRepository.save(course);

                courseEventProducer.publish(
                                savedCourse,
                                "COURSE_CREATED");

                return new CourseResponse(savedCourse);
        }

        // public UserResponse getUserFromUserService(Long userId) {

        // return userServiceClient.getUserById(userId);
        // }

        public UserCoursesResponse getUserWithCourses(Long userId) {

                UserResponse user = userServiceClient.getUserById(userId);

                List<CourseResponse> courses = courseRepository
                                .findByInstructorId(userId)
                                .stream()
                                .map(CourseResponse::new)
                                .toList();

                return new UserCoursesResponse(user, courses);
        }

        /*
         * GET ALL COURSES
         *
         * Only published + active courses are returned
         * to normal users.
         *
         * ADMIN can see everything.
         */
        public List<CourseResponse> getAllCourses() {

                boolean isAdmin = hasRole("ROLE_ADMIN");

                List<Course> courses;

                if (isAdmin) {

                        courses = courseRepository.findAll();

                } else {

                        courses = courseRepository
                                        .findByPublishedTrueAndActiveTrue();
                }

                return courses.stream()
                                .map(CourseResponse::new)
                                .toList();
        }

        /*
         * GET COURSE BY ID
         */
        // public CourseResponse getCourseById(Long id) {

        // Course course = courseRepository.findById(id)
        // .orElseThrow(() -> new CourseNotFoundException(
        // "Course not found"));

        // /*
        // * Non-admin users cannot view inactive/unpublished
        // * courses.
        // */
        // if (!hasRole("ROLE_ADMIN")) {

        // if (!course.isPublished()
        // || !course.isActive()) {

        // throw new RuntimeException(
        // "Course not available");
        // }
        // }

        // return new CourseResponse(course);
        // }

        public CourseResponse getCourseById(Long id) {

                Course course = courseRepository.findById(id)
                                .orElseThrow(() -> new CourseNotFoundException(
                                                "Course not found"));

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null ||
                                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

                        throw new AccessDeniedException(
                                        "User is not authenticated");
                }

                boolean isAdmin = hasRole("ROLE_ADMIN");

                boolean isInstructor = hasRole(
                                "ROLE_INSTRUCTOR");

                /*
                 * ADMIN:
                 * Can view any course.
                 */
                if (isAdmin) {

                        return new CourseResponse(course);
                }

                /*
                 * INSTRUCTOR:
                 * Can view their own course even if
                 * it is unpublished or inactive.
                 */
                if (isInstructor) {

                        if (!course.getInstructorId()
                                        .equals(user.getUserId())) {

                                throw new AccessDeniedException(
                                                "You are not the owner of this course");
                        }

                        return new CourseResponse(course);
                }

                /*
                 * STUDENT:
                 * Can only view published + active courses.
                 */
                if (!course.isPublished()
                                || !course.isActive()) {

                        throw new RuntimeException(
                                        "Course not available");
                }

                return new CourseResponse(course);
        }

        /*
         * UPDATE COURSE
         *
         * ADMIN can update any course.
         *
         * INSTRUCTOR can update only their own course.
         */
        public CourseResponse updateCourse(
                        Long id,
                        UpdateCourseRequest request) {

                Course course = courseRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Course not found"));

                if (!canManageCourse(course)) {

                        throw new AccessDeniedException(
                                        "You are not allowed to update this course");
                }

                if (request.getTitle() != null) {

                        course.setTitle(
                                        request.getTitle());
                }

                if (request.getDescription() != null) {

                        course.setDescription(
                                        request.getDescription());
                }

                if (request.getCategory() != null) {

                        course.setCategory(
                                        request.getCategory());
                }

                if (request.getSkills() != null) {

                        course.setSkills(
                                        request.getSkills()
                                                        .stream()
                                                        .map(String::trim)
                                                        .filter(skill -> !skill.isBlank())
                                                        .collect(java.util.stream.Collectors.toSet()));
                }

                // if (request.getPublished() != null) {

                // course.setPublished(
                // request.getPublished());
                // }

                if (request.getActive() != null) {

                        course.setActive(
                                        request.getActive());
                }

                Course updatedCourse = courseRepository.save(course);

                courseEventProducer.publish(
                                updatedCourse,
                                "COURSE_UPDATED");

                return new CourseResponse(updatedCourse);
        }

        /*
         * PUBLISH COURSE
         *
         * ADMIN can publish any course.
         * INSTRUCTOR can publish only their own course.
         */
        public CourseResponse publishCourse(Long id) {

                Course course = courseRepository.findById(id)
                                .orElseThrow(() -> new CourseNotFoundException(
                                                "Course not found"));

                if (!canManageCourse(course)) {
                        throw new AccessDeniedException(
                                        "You are not allowed to publish this course");
                }

                if (!course.isActive()) {
                        throw new IllegalStateException(
                                        "Inactive course cannot be published");
                }

                if (course.isPublished()) {
                        throw new CourseAlreadyPublishedException(
                                        "Course is already published");
                }

                course.setPublished(true);

                Course savedCourse = courseRepository.save(course);

                courseEventProducer.publish(
                                savedCourse,
                                "COURSE_PUBLISHED");

                return new CourseResponse(savedCourse);
        }

        /*
         * UNPUBLISH COURSE
         *
         * ADMIN can unpublish any course.
         * INSTRUCTOR can unpublish only their own course.
         */
        public CourseResponse unpublishCourse(Long id) {

                Course course = courseRepository.findById(id)
                                .orElseThrow(() -> new CourseNotFoundException(
                                                "Course not found"));

                if (!canManageCourse(course)) {
                        throw new AccessDeniedException(
                                        "You are not allowed to unpublish this course");
                }

                if (!course.isPublished()) {
                        throw new CourseStateException(
                                        "Course is already unpublished");
                }

                course.setPublished(false);

                Course savedCourse = courseRepository.save(course);

                courseEventProducer.publish(
                                savedCourse,
                                "COURSE_UNPUBLISHED");

                return new CourseResponse(savedCourse);
        }

        /*
         * DELETE COURSE
         *
         * ADMIN can delete any course.
         *
         * INSTRUCTOR can delete only their own course.
         */
        public void deleteCourse(Long id) {

                Course course = courseRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Course not found"));

                if (!canManageCourse(course)) {

                        throw new AccessDeniedException(
                                        "You are not allowed to delete this course");
                }

                courseRepository.delete(course);
        }

        /*
         * CHECK WHETHER CURRENT USER CAN MANAGE COURSE
         */
        // private boolean canManageCourse(
        // Course course) {

        // if (hasRole("ROLE_ADMIN")) {

        // return true;
        // }

        // if (!hasRole("ROLE_INSTRUCTOR")) {

        // return false;
        // }

        // /*
        // * IMPORTANT:
        // *
        // * At this stage the JWT contains the user's email,
        // * not their User Service database ID.
        // *
        // * Therefore, for now we compare the instructorId
        // * supplied in the authenticated workflow separately.
        // *
        // * We will improve this when Course Service communicates
        // * with User Service.
        // */
        // Authentication authentication = SecurityContextHolder
        // .getContext()
        // .getAuthentication();

        // return isInstructorOwner(
        // course,
        // authentication);
        // }

        private boolean canManageCourse(Course course) {

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                System.out.println("=== COURSE OWNERSHIP DEBUG ===");
                System.out.println("Authentication: " + authentication);
                System.out.println("Principal: " + authentication.getPrincipal());
                System.out.println("Principal class: "
                                + authentication.getPrincipal().getClass().getName());
                System.out.println("Course instructor ID: "
                                + course.getInstructorId());

                if (hasRole("ROLE_ADMIN")) {
                        System.out.println("OWNER CHECK: ADMIN -> ALLOWED");
                        return true;
                }

                if (!hasRole("ROLE_INSTRUCTOR")) {
                        System.out.println("OWNER CHECK: NOT INSTRUCTOR -> DENIED");
                        return false;
                }

                if (!(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {

                        System.out.println(
                                        "OWNER CHECK: PRINCIPAL IS NOT AuthenticatedUser -> DENIED");

                        return false;
                }

                System.out.println("Authenticated user ID: "
                                + authenticatedUser.getUserId());

                boolean owner = course.getInstructorId()
                                .equals(authenticatedUser.getUserId());

                System.out.println("OWNER CHECK RESULT: " + owner);
                System.out.println("==============================");

                return owner;
        }

        /*
         * Temporary instructor ownership check.
         *
         * The clean solution will be to include user ID in JWT.
         */
        private boolean isInstructorOwner(
                        Course course,
                        Authentication authentication) {

                if (!(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {

                        return false;
                }

                return course.getInstructorId()
                                .equals(authenticatedUser.getUserId());
        }

        /*
         * ROLE CHECK
         */
        private boolean hasRole(String role) {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null) {
                        return false;
                }

                return authentication.getAuthorities()
                                .stream()
                                .anyMatch(authority -> authority.getAuthority()
                                                .equals(role));
        }

        private Long getCurrentUserId() {

                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (!(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {

                        throw new AccessDeniedException(
                                        "User is not authenticated");
                }

                return authenticatedUser.getUserId();
        }

        public List<CourseResponse> searchCourses(
                        String title,
                        String category) {

                List<Course> courses;

                boolean isAdmin = hasRole("ROLE_ADMIN");

                if (isAdmin) {

                        if (title != null && !title.isBlank()
                                        && category != null && !category.isBlank()) {

                                courses = courseRepository
                                                .findAll()
                                                .stream()
                                                .filter(course -> course.getTitle()
                                                                .toLowerCase()
                                                                .contains(title.toLowerCase()))
                                                .filter(course -> course.getCategory()
                                                                .equalsIgnoreCase(category))
                                                .toList();

                        } else if (title != null && !title.isBlank()) {

                                courses = courseRepository
                                                .findAll()
                                                .stream()
                                                .filter(course -> course.getTitle()
                                                                .toLowerCase()
                                                                .contains(title.toLowerCase()))
                                                .toList();

                        } else if (category != null && !category.isBlank()) {

                                courses = courseRepository
                                                .findAll()
                                                .stream()
                                                .filter(course -> course.getCategory()
                                                                .equalsIgnoreCase(category))
                                                .toList();

                        } else {

                                courses = courseRepository.findAll();
                        }

                } else {

                        if (title != null && !title.isBlank()
                                        && category != null && !category.isBlank()) {

                                courses = courseRepository
                                                .findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
                                                                category,
                                                                title);

                        } else if (title != null && !title.isBlank()) {

                                courses = courseRepository
                                                .findByTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
                                                                title);

                        } else if (category != null && !category.isBlank()) {

                                courses = courseRepository
                                                .findByCategoryIgnoreCaseAndPublishedTrueAndActiveTrue(
                                                                category);

                        } else {

                                courses = courseRepository
                                                .findByPublishedTrueAndActiveTrue();
                        }
                }

                return courses.stream()
                                .map(CourseResponse::new)
                                .toList();
        }

        public PageResponse<CourseResponse> searchCoursesPaged(
                        String title,
                        String category,
                        int page,
                        int size,
                        String sortBy,
                        String direction) {

                if (page < 0) {
                        throw new IllegalArgumentException(
                                        "Page must be greater than or equal to 0");
                }

                if (size < 1 || size > 100) {
                        throw new IllegalArgumentException(
                                        "Size must be between 1 and 100");
                }

                Sort.Direction sortDirection;

                try {
                        sortDirection = Sort.Direction.fromString(direction);
                } catch (Exception e) {
                        throw new IllegalArgumentException(
                                        "Direction must be asc or desc");
                }

                Pageable pageable = PageRequest.of(
                                page,
                                size,
                                Sort.by(sortDirection, sortBy));

                Page<Course> courses;

                boolean isAdmin = hasRole("ROLE_ADMIN");

                if (isAdmin) {

                        /*
                         * ADMIN can see all courses.
                         *
                         * Filtering for ADMIN is performed below.
                         */
                        courses = courseRepository.findAll(pageable);

                } else {

                        /*
                         * Normal users only see:
                         *
                         * published = true
                         * active = true
                         */

                        if (title != null && !title.isBlank()
                                        && category != null && !category.isBlank()) {

                                courses = courseRepository
                                                .findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
                                                                category,
                                                                title,
                                                                pageable);

                        } else if (title != null && !title.isBlank()) {

                                courses = courseRepository
                                                .findByTitleContainingIgnoreCaseAndPublishedTrueAndActiveTrue(
                                                                title,
                                                                pageable);

                        } else if (category != null && !category.isBlank()) {

                                courses = courseRepository
                                                .findByCategoryIgnoreCaseAndPublishedTrueAndActiveTrue(
                                                                category,
                                                                pageable);

                        } else {

                                courses = courseRepository
                                                .findByPublishedTrueAndActiveTrue(
                                                                pageable);
                        }
                }

                List<CourseResponse> content = courses.getContent()
                                .stream()
                                .map(CourseResponse::new)
                                .toList();

                return new PageResponse<>(
                                content,
                                courses.getNumber(),
                                courses.getSize(),
                                courses.getTotalElements(),
                                courses.getTotalPages(),
                                courses.isFirst(),
                                courses.isLast());
        }

        public CourseResponse activateCourse(Long id) {

                Course course = courseRepository.findById(id)
                                .orElseThrow(() -> new CourseNotFoundException(
                                                "Course not found"));

                if (!canManageCourse(course)) {
                        throw new AccessDeniedException(
                                        "You are not allowed to activate this course");
                }

                if (course.isActive()) {
                        throw new CourseStateException(
                                        "Course is already active");
                }

                course.setActive(true);

                Course savedCourse = courseRepository.save(course);

                courseEventProducer.publish(
                                savedCourse,
                                "COURSE_ACTIVATED");

                return new CourseResponse(savedCourse);
        }

        public CourseResponse deactivateCourse(Long id) {

                Course course = courseRepository.findById(id)
                                .orElseThrow(() -> new CourseNotFoundException(
                                                "Course not found"));

                if (!canManageCourse(course)) {
                        throw new AccessDeniedException(
                                        "You are not allowed to deactivate this course");
                }

                if (!course.isActive()) {
                        throw new CourseStateException(
                                        "Course is already inactive");
                }

                course.setActive(false);

                Course savedCourse = courseRepository.save(course);

                courseEventProducer.publish(
                                savedCourse,
                                "COURSE_DEACTIVATED");

                return new CourseResponse(savedCourse);
        }

}