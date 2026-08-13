        package com.learningplatform.course_service.controller;

        import com.learningplatform.course_service.dto.CourseResponse;
        import com.learningplatform.course_service.dto.CreateCourseRequest;
        import com.learningplatform.course_service.dto.UpdateCourseRequest;
        import com.learningplatform.course_service.dto.UserCoursesResponse;
        import com.learningplatform.course_service.dto.UserResponse;
        import com.learningplatform.course_service.service.CourseService;
        import com.learningplatform.course_service.dto.PageResponse;

        import jakarta.validation.Valid;

        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;

        @RestController
        @RequestMapping("/api/courses")
        public class CourseController {

                private final CourseService courseService;

                public CourseController(CourseService courseService) {
                        this.courseService = courseService;
                }

                /*
                * CREATE COURSE
                *
                * INSTRUCTOR or ADMIN
                */
                @PostMapping
                public ResponseEntity<CourseResponse> createCourse(
                                @Valid @RequestBody CreateCourseRequest request) {

                        CourseResponse response = courseService.createCourse(request);

                        return ResponseEntity
                                        .status(HttpStatus.CREATED)
                                        .body(response);
                }

                @PostMapping("/{id}/publish")
                public ResponseEntity<CourseResponse> publishCourse(
                                @PathVariable Long id) {

                        return ResponseEntity.ok(
                                        courseService.publishCourse(id));
                }

                @PostMapping("/{id}/unpublish")
                public ResponseEntity<CourseResponse> unpublishCourse(
                                @PathVariable Long id) {

                        return ResponseEntity.ok(
                                        courseService.unpublishCourse(id));
                }

                @PostMapping("/{id}/activate")
                public ResponseEntity<CourseResponse> activateCourse(
                                @PathVariable Long id) {

                        return ResponseEntity.ok(
                                        courseService.activateCourse(id));
                }

                @PostMapping("/{id}/deactivate")
                public ResponseEntity<CourseResponse> deactivateCourse(
                                @PathVariable Long id) {

                        return ResponseEntity.ok(
                                        courseService.deactivateCourse(id));
                }

                /*
                * GET ALL COURSES
                *
                * STUDENT / INSTRUCTOR:
                * published + active courses
                *
                * ADMIN:
                * all courses
                */
                @GetMapping
                public ResponseEntity<List<CourseResponse>> getAllCourses() {

                        return ResponseEntity.ok(
                                        courseService.getAllCourses());
                }

                @GetMapping("/search")
                public ResponseEntity<List<CourseResponse>> searchCourses(
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) String category) {

                        return ResponseEntity.ok(
                                        courseService.searchCourses(
                                                        title,
                                                        category));
                }

                // @GetMapping("/users/{userId}")
                // public ResponseEntity<UserResponse> getUserFromUserService(
                // @PathVariable Long userId) {

                // return ResponseEntity.ok(
                // courseService.getUserFromUserService(userId));
                // }

                @GetMapping("/users/{id}")
                public ResponseEntity<UserCoursesResponse> getUserWithCourses(
                                @PathVariable Long id) {

                        return ResponseEntity.ok(
                                        courseService.getUserWithCourses(id));
                }

                /*
                * GET COURSE BY ID
                */
                @GetMapping("/{id}")
                public ResponseEntity<CourseResponse> getCourseById(
                                @PathVariable Long id) {

                        return ResponseEntity.ok(
                                        courseService.getCourseById(id));
                }

                @GetMapping("/paged")
                public ResponseEntity<PageResponse<CourseResponse>> getCoursesPaged(
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                @RequestParam(defaultValue = "desc") String direction) {

                        return ResponseEntity.ok(
                                        courseService.searchCoursesPaged(
                                                        null,
                                                        null,
                                                        page,
                                                        size,
                                                        sortBy,
                                                        direction));
                }

                @GetMapping("/search/paged")
                public ResponseEntity<PageResponse<CourseResponse>> searchCoursesPaged(
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) String category,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                @RequestParam(defaultValue = "desc") String direction) {

                        return ResponseEntity.ok(
                                        courseService.searchCoursesPaged(
                                                        title,
                                                        category,
                                                        page,
                                                        size,
                                                        sortBy,
                                                        direction));
                }

                /*
                * UPDATE COURSE
                *
                * INSTRUCTOR:
                * own course only
                *
                * ADMIN:
                * any course
                */
                @PutMapping("/{id}")
                public ResponseEntity<CourseResponse> updateCourse(
                                @PathVariable Long id,
                                @Valid @RequestBody UpdateCourseRequest request) {

                        CourseResponse response = courseService.updateCourse(
                                        id,
                                        request);

                        return ResponseEntity.ok(response);
                }

                /*
                * DELETE COURSE
                *
                * INSTRUCTOR:
                * own course only
                *
                * ADMIN:
                * any course
                */
                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteCourse(
                                @PathVariable Long id) {

                        courseService.deleteCourse(id);

                        return ResponseEntity.noContent().build();
                }
        }