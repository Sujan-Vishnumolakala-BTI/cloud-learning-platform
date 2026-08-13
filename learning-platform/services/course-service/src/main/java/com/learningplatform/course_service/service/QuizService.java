// package com.learningplatform.course_service.service;

// import com.learningplatform.course_service.dto.CreateQuizRequest;
// import com.learningplatform.course_service.dto.QuizResponse;
// import com.learningplatform.course_service.entity.Course;
// import com.learningplatform.course_service.entity.Lesson;
// import com.learningplatform.course_service.entity.Module;
// import com.learningplatform.course_service.entity.Quiz;

// import com.learningplatform.course_service.exception.CourseNotFoundException;

// import com.learningplatform.course_service.repository.CourseRepository;
// import com.learningplatform.course_service.repository.LessonRepository;
// import com.learningplatform.course_service.repository.ModuleRepository;
// import com.learningplatform.course_service.repository.QuizRepository;

// import com.learningplatform.course_service.security.AuthenticatedUser;

// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.ArrayList;
// import java.util.List;

// @Service
// public class QuizService {

//         private final QuizRepository quizRepository;

//         private final LessonRepository lessonRepository;

//         private final ModuleRepository moduleRepository;

//         private final CourseRepository courseRepository;

//         public QuizService(
//                         QuizRepository quizRepository,
//                         LessonRepository lessonRepository,
//                         ModuleRepository moduleRepository,
//                         CourseRepository courseRepository) {

//                 this.quizRepository = quizRepository;

//                 this.lessonRepository = lessonRepository;

//                 this.moduleRepository = moduleRepository;

//                 this.courseRepository = courseRepository;
//         }

//         /*
//          * =========================================================
//          * CREATE QUIZ
//          * =========================================================
//          *
//          * POST /api/lessons/{lessonId}/quiz
//          */
//         @Transactional
//         public QuizResponse createQuiz(
//                         Long lessonId,
//                         CreateQuizRequest request) {

//                 Lesson lesson = lessonRepository.findById(lessonId)
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Lesson not found with id: "
//                                                                 + lessonId));

//                 Course course = getCourseFromLesson(lesson);

//                 checkCourseOwnership(course);

//                 if (quizRepository.existsByLessonId(
//                                 lessonId)) {

//                         throw new RuntimeException(
//                                         "A quiz already exists for this lesson");
//                 }

//                 Quiz quiz = new Quiz();

//                 quiz.setLessonId(lessonId);

//                 quiz.setTitle(
//                                 request.getTitle());

//                 quiz.setDescription(
//                                 request.getDescription());

//                 quiz.setPassingScore(
//                                 request.getPassingScore());

//                 Quiz saved = quizRepository.save(quiz);

//                 return toResponse(saved);
//         }

//         /*
//          * =========================================================
//          * GET QUIZ FOR LESSON
//          * =========================================================
//          *
//          * GET /api/lessons/{lessonId}/quiz
//          */
//         public QuizResponse getQuizByLesson(
//                         Long lessonId) {

//                 Quiz quiz = quizRepository
//                                 .findByLessonId(lessonId)
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Quiz not found for lesson"));

//                 return toResponse(quiz);
//         }

//         /*
//          * =========================================================
//          * GET ALL QUIZZES FOR COURSE
//          * =========================================================
//          *
//          * GET /api/courses/{courseId}/quizzes
//          */
//         public List<QuizResponse> getQuizzesByCourse(
//                         Long courseId) {

//                 Course course = courseRepository.findById(courseId)
//                                 .orElseThrow(() -> new CourseNotFoundException(
//                                                 "Course not found"));

//                 /*
//                  * Instructor/Admin can manage.
//                  * Students should not use this endpoint
//                  * for instructor management.
//                  */
//                 checkCourseOwnership(course);

//                 /*
//                  * Find all modules belonging to course.
//                  */
//                 List<Module> modules = moduleRepository.findByCourseIdOrderByOrderIndexAsc(
//                                 courseId);

//                 if (modules.isEmpty()) {
//                         return List.of();
//                 }

//                 /*
//                  * Find all lessons belonging to those modules.
//                  */
//                 List<Long> moduleIds = modules.stream()
//                                 .map(Module::getId)
//                                 .toList();

//                 List<Lesson> lessons = lessonRepository.findByModuleIdIn(
//                                 moduleIds);

//                 if (lessons.isEmpty()) {
//                         return List.of();
//                 }

//                 /*
//                  * Extract lesson IDs.
//                  */
//                 List<Long> lessonIds = lessons.stream()
//                                 .map(Lesson::getId)
//                                 .toList();

//                 /*
//                  * Find quizzes for those lessons.
//                  */
//                 List<Quiz> quizzes = quizRepository.findByLessonIdIn(
//                                 lessonIds);

//                 return quizzes.stream()
//                                 .map(this::toResponse)
//                                 .toList();
//         }

//         /*
//          * =========================================================
//          * GET QUIZ BY ID
//          * =========================================================
//          *
//          * GET /api/quizzes/{quizId}
//          */
//         public QuizResponse getQuizById(
//                         Long quizId) {

//                 Quiz quiz = quizRepository.findById(quizId)
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Quiz not found with id: "
//                                                                 + quizId));

//                 return toResponse(quiz);
//         }

//         /*
//          * =========================================================
//          * UPDATE QUIZ
//          * =========================================================
//          *
//          * PUT /api/quizzes/{quizId}
//          */
//         @Transactional
//         public QuizResponse updateQuiz(
//                         Long quizId,
//                         CreateQuizRequest request) {

//                 Quiz quiz = quizRepository.findById(quizId)
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Quiz not found with id: "
//                                                                 + quizId));

//                 /*
//                  * Find current lesson.
//                  */
//                 Lesson currentLesson = lessonRepository.findById(
//                                 quiz.getLessonId())
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Lesson not found"));

//                 Course currentCourse = getCourseFromLesson(
//                                 currentLesson);
//                 System.out.println("========== QUIZ OWNERSHIP CHECK ==========");
//                 System.out.println("LESSON ID: " + lessonId);
//                 System.out.println("MODULE ID: " + module.getId());
//                 System.out.println("COURSE ID: " + course.getId());
//                 System.out.println("COURSE INSTRUCTOR ID: " + course.getInstructorId());

//                 Authentication authentication = SecurityContextHolder
//                                 .getContext()
//                                 .getAuthentication();

//                 if (authentication != null) {

//                         System.out.println(
//                                         "AUTHENTICATED USER: "
//                                                         + authentication.getName());

//                         System.out.println(
//                                         "AUTHORITIES: "
//                                                         + authentication.getAuthorities());

//                         if (authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser) {

//                                 System.out.println(
//                                                 "AUTHENTICATED USER ID: "
//                                                                 + authenticatedUser.getUserId());
//                         }
//                 }

//                 System.out.println("==========================================");

//                 // checkCourseOwnership(course);
//                 checkCourseOwnership(
//                                 currentCourse);

//                 /*
//                  * If the frontend sends a different lessonId,
//                  * verify the new lesson belongs to the same course.
//                  */
//                 if (request.getLessonId() != null
//                                 && !request.getLessonId()
//                                                 .equals(quiz.getLessonId())) {

//                         Lesson newLesson = lessonRepository.findById(
//                                         request.getLessonId())
//                                         .orElseThrow(() -> new RuntimeException(
//                                                         "New lesson not found"));

//                         Course newCourse = getCourseFromLesson(
//                                         newLesson);

//                         if (!newCourse.getId()
//                                         .equals(currentCourse.getId())) {

//                                 throw new AccessDeniedException(
//                                                 "Quiz cannot be moved to another course");
//                         }

//                         /*
//                          * A lesson can have only one quiz.
//                          */
//                         if (quizRepository.existsByLessonId(
//                                         request.getLessonId())) {

//                                 Quiz existingQuiz = quizRepository
//                                                 .findByLessonId(
//                                                                 request.getLessonId())
//                                                 .orElse(null);

//                                 if (existingQuiz != null
//                                                 && !existingQuiz.getId()
//                                                                 .equals(quizId)) {

//                                         throw new RuntimeException(
//                                                         "A quiz already exists for this lesson");
//                                 }
//                         }

//                         quiz.setLessonId(
//                                         request.getLessonId());
//                 }

//                 quiz.setTitle(
//                                 request.getTitle());

//                 quiz.setDescription(
//                                 request.getDescription());

//                 quiz.setPassingScore(
//                                 request.getPassingScore());

//                 Quiz updated = quizRepository.save(quiz);

//                 return toResponse(updated);
//         }

//         /*
//          * =========================================================
//          * DELETE QUIZ
//          * =========================================================
//          *
//          * DELETE /api/quizzes/{quizId}
//          */
//         @Transactional
//         public void deleteQuiz(
//                         Long quizId) {

//                 Quiz quiz = quizRepository.findById(quizId)
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Quiz not found with id: "
//                                                                 + quizId));

//                 Lesson lesson = lessonRepository.findById(
//                                 quiz.getLessonId())
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Lesson not found"));

//                 Course course = getCourseFromLesson(
//                                 lesson);

//                 checkCourseOwnership(
//                                 course);

//                 quizRepository.delete(quiz);
//         }

//         /*
//          * =========================================================
//          * GET COURSE ID FROM QUIZ
//          * =========================================================
//          */
//         public Long getCourseId(
//                         Long quizId) {

//                 Quiz quiz = quizRepository.findById(quizId)
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Quiz not found"));

//                 Lesson lesson = lessonRepository.findById(
//                                 quiz.getLessonId())
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Lesson not found"));

//                 Module module = moduleRepository.findById(
//                                 lesson.getModuleId())
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Module not found"));

//                 return module.getCourseId();
//         }

//         /*
//          * =========================================================
//          * GET COURSE FROM LESSON
//          * =========================================================
//          */
//         private Course getCourseFromLesson(
//                         Lesson lesson) {

//                 Module module = moduleRepository.findById(
//                                 lesson.getModuleId())
//                                 .orElseThrow(() -> new RuntimeException(
//                                                 "Module not found"));

//                 return courseRepository.findById(
//                                 module.getCourseId())
//                                 .orElseThrow(() -> new CourseNotFoundException(
//                                                 "Course not found"));
//         }

//         /*
//          * =========================================================
//          * COURSE OWNERSHIP
//          * =========================================================
//          */
//         private void checkCourseOwnership(
//                         Course course) {

//                 Authentication authentication = SecurityContextHolder
//                                 .getContext()
//                                 .getAuthentication();

//                 if (hasRole("ROLE_ADMIN")) {
//                         return;
//                 }

//                 if (!hasRole("ROLE_INSTRUCTOR")) {

//                         throw new AccessDeniedException(
//                                         "Only instructors or admins can manage quizzes");
//                 }

//                 if (authentication == null
//                                 || !(authentication.getPrincipal() instanceof AuthenticatedUser authenticatedUser)) {

//                         throw new AccessDeniedException(
//                                         "Invalid authenticated user");
//                 }

//                 if (!course.getInstructorId()
//                                 .equals(authenticatedUser.getUserId())) {

//                         throw new AccessDeniedException(
//                                         "You are not the owner of this course");
//                 }
//         }

//         /*
//          * =========================================================
//          * ROLE CHECK
//          * =========================================================
//          */
//         private boolean hasRole(
//                         String role) {

//                 Authentication authentication = SecurityContextHolder
//                                 .getContext()
//                                 .getAuthentication();

//                 if (authentication == null) {
//                         return false;
//                 }

//                 return authentication
//                                 .getAuthorities()
//                                 .stream()
//                                 .anyMatch(authority -> authority.getAuthority()
//                                                 .equals(role));
//         }

//         /*
//          * =========================================================
//          * RESPONSE MAPPER
//          * =========================================================
//          */
//         private QuizResponse toResponse(
//                         Quiz quiz) {

//                 return new QuizResponse(
//                                 quiz.getId(),
//                                 quiz.getLessonId(),
//                                 quiz.getTitle(),
//                                 quiz.getDescription(),
//                                 quiz.getPassingScore(),
//                                 quiz.getCreatedAt(),
//                                 quiz.getUpdatedAt());
//         }
// }

package com.learningplatform.course_service.service;

import com.learningplatform.course_service.dto.CreateQuizRequest;
import com.learningplatform.course_service.dto.QuizResponse;
import com.learningplatform.course_service.entity.Course;
import com.learningplatform.course_service.entity.Lesson;
import com.learningplatform.course_service.entity.Module;
import com.learningplatform.course_service.entity.Quiz;
import com.learningplatform.course_service.exception.CourseNotFoundException;
import com.learningplatform.course_service.repository.CourseRepository;
import com.learningplatform.course_service.repository.LessonRepository;
import com.learningplatform.course_service.repository.ModuleRepository;
import com.learningplatform.course_service.repository.QuizRepository;
import com.learningplatform.course_service.security.AuthenticatedUser;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    public QuizService(
            QuizRepository quizRepository,
            LessonRepository lessonRepository,
            ModuleRepository moduleRepository,
            CourseRepository courseRepository) {

        this.quizRepository = quizRepository;
        this.lessonRepository = lessonRepository;
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
    }

    /*
     * CREATE QUIZ
     *
     * lessonId comes from:
     *
     * POST /api/lessons/{lessonId}/quiz
     */
    @Transactional
    public QuizResponse createQuiz(
            Long lessonId,
            CreateQuizRequest request) {

        // =========================
        // FIND LESSON
        // =========================

        Lesson lesson = lessonRepository
                .findById(lessonId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Lesson not found with id: "
                                        + lessonId));

        // =========================
        // FIND MODULE
        // =========================

        Module module = moduleRepository
                .findById(lesson.getModuleId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Module not found"));

        // =========================
        // FIND COURSE
        // =========================

        Course course = courseRepository
                .findById(module.getCourseId())
                .orElseThrow(() ->
                        new CourseNotFoundException(
                                "Course not found"));

        // =========================
        // DEBUG OWNERSHIP
        // =========================

        System.out.println(
                "========== QUIZ OWNERSHIP CHECK ==========");

        System.out.println(
                "LESSON ID: "
                        + lessonId);

        System.out.println(
                "MODULE ID: "
                        + module.getId());

        System.out.println(
                "COURSE ID: "
                        + course.getId());

        System.out.println(
                "COURSE INSTRUCTOR ID: "
                        + course.getInstructorId());

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication != null) {

            System.out.println(
                    "AUTHENTICATED USER: "
                            + authentication.getName());

            System.out.println(
                    "AUTHORITIES: "
                            + authentication
                                    .getAuthorities());

            if (authentication.getPrincipal()
                    instanceof AuthenticatedUser authenticatedUser) {

                System.out.println(
                        "AUTHENTICATED USER ID: "
                                + authenticatedUser.getUserId());
            }
        }

        System.out.println(
                "==========================================");

        // =========================
        // CHECK COURSE OWNERSHIP
        // =========================

        checkCourseOwnership(course);

        // =========================
        // CHECK EXISTING QUIZ
        // =========================

        if (quizRepository.existsByLessonId(lessonId)) {

            throw new RuntimeException(
                    "A quiz already exists for this lesson");
        }

        // =========================
        // CREATE QUIZ
        // =========================

        Quiz quiz = new Quiz();

        quiz.setLessonId(lessonId);

        quiz.setTitle(
                request.getTitle());

        quiz.setDescription(
                request.getDescription());

        quiz.setPassingScore(
                request.getPassingScore());

        // =========================
        // SAVE
        // =========================

        Quiz saved =
                quizRepository.save(quiz);

        System.out.println(
                "QUIZ CREATED SUCCESSFULLY. ID: "
                        + saved.getId());

        return toResponse(saved);
    }

    /*
     * GET QUIZ BY LESSON
     */
    public QuizResponse getQuizByLesson(
            Long lessonId) {

        Quiz quiz =
                quizRepository
                        .findByLessonId(lessonId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quiz not found for lesson"));

        return toResponse(quiz);
    }

    /*
     * GET QUIZ BY ID
     */
    public QuizResponse getQuizById(
            Long quizId) {

        Quiz quiz =
                quizRepository
                        .findById(quizId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quiz not found with id: "
                                                + quizId));

        return toResponse(quiz);
    }

    /*
     * GET COURSE ID FROM QUIZ
     */
    public Long getCourseId(
            Long quizId) {

        Quiz quiz =
                quizRepository
                        .findById(quizId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Quiz not found"));

        Lesson lesson =
                lessonRepository
                        .findById(
                                quiz.getLessonId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Lesson not found"));

        Module module =
                moduleRepository
                        .findById(
                                lesson.getModuleId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Module not found"));

        return module.getCourseId();
    }

    /*
     * COURSE OWNERSHIP CHECK
     *
     * ADMIN:
     * allowed
     *
     * INSTRUCTOR:
     * only own course
     */
    private void checkCourseOwnership(
            Course course) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "User is not authenticated");
        }

        // =========================
        // ADMIN
        // =========================

        if (hasRole("ROLE_ADMIN")) {

            return;
        }

        // =========================
        // INSTRUCTOR
        // =========================

        if (!hasRole("ROLE_INSTRUCTOR")) {

            throw new AccessDeniedException(
                    "Only instructors or admins can manage quizzes");
        }

        // =========================
        // AUTHENTICATED USER
        // =========================

        if (!(authentication.getPrincipal()
                instanceof AuthenticatedUser authenticatedUser)) {

            throw new AccessDeniedException(
                    "Invalid authenticated user");
        }

        Long instructorId =
                authenticatedUser.getUserId();

        Long courseInstructorId =
                course.getInstructorId();

        System.out.println(
                "QUIZ OWNERSHIP: authenticated user = "
                        + instructorId);

        System.out.println(
                "QUIZ OWNERSHIP: course instructor = "
                        + courseInstructorId);

        // =========================
        // OWNER CHECK
        // =========================

        if (courseInstructorId == null ||
                !courseInstructorId.equals(instructorId)) {

            throw new AccessDeniedException(
                    "You are not the owner of this course");
        }
    }

    /*
     * CHECK ROLE
     */
    private boolean hasRole(
            String role) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null) {

            return false;
        }

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority
                                .getAuthority()
                                .equals(role));
    }

    /*
     * ENTITY -> RESPONSE
     */
    private QuizResponse toResponse(
            Quiz quiz) {

        return new QuizResponse(
                quiz.getId(),
                quiz.getLessonId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getPassingScore(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }

    public List<QuizResponse> getQuizzesByCourse(
        Long courseId) {

    Course course =
            courseRepository.findById(courseId)
                    .orElseThrow(() ->
                            new CourseNotFoundException(
                                    "Course not found"));

    /*
     * Instructor must own the course.
     * Admin is allowed.
     */
    checkCourseOwnership(course);

    /*
     * Get all modules for this course.
     */
    List<Module> modules =
            moduleRepository
                    .findByCourseIdOrderByOrderIndexAsc(
                            courseId);

    if (modules.isEmpty()) {
        return List.of();
    }

    /*
     * Get module IDs.
     */
    List<Long> moduleIds =
            modules.stream()
                    .map(Module::getId)
                    .toList();

    /*
     * Get all lessons belonging
     * to those modules.
     */
    List<Lesson> lessons =
            lessonRepository
                    .findByModuleIdIn(
                            moduleIds);

    if (lessons.isEmpty()) {
        return List.of();
    }

    /*
     * Get lesson IDs.
     */
    List<Long> lessonIds =
            lessons.stream()
                    .map(Lesson::getId)
                    .toList();

    /*
     * Get quizzes belonging
     * to those lessons.
     */
    List<Quiz> quizzes =
            quizRepository
                    .findByLessonIdIn(
                            lessonIds);

    return quizzes.stream()
            .map(this::toResponse)
            .toList();
}

@Transactional
public QuizResponse updateQuiz(
        Long quizId,
        CreateQuizRequest request) {

    Quiz quiz =
            quizRepository.findById(quizId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Quiz not found with id: "
                                            + quizId));

    /*
     * Find the lesson belonging
     * to this quiz.
     */
    Lesson lesson =
            lessonRepository.findById(
                    quiz.getLessonId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Lesson not found"));

    /*
     * Find the course through:
     *
     * Quiz -> Lesson -> Module -> Course
     */
    Module module =
            moduleRepository.findById(
                    lesson.getModuleId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Module not found"));

    Course course =
            courseRepository.findById(
                    module.getCourseId())
                    .orElseThrow(() ->
                            new CourseNotFoundException(
                                    "Course not found"));

    /*
     * Verify instructor owns course.
     */
    checkCourseOwnership(course);

    /*
     * Update fields.
     */
    quiz.setTitle(
            request.getTitle());

    quiz.setDescription(
            request.getDescription());

    quiz.setPassingScore(
            request.getPassingScore());

    Quiz updated =
            quizRepository.save(quiz);

    return toResponse(updated);
}

@Transactional
public void deleteQuiz(
        Long quizId) {

    Quiz quiz =
            quizRepository.findById(quizId)
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Quiz not found with id: "
                                            + quizId));

    /*
     * Find lesson.
     */
    Lesson lesson =
            lessonRepository.findById(
                    quiz.getLessonId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Lesson not found"));

    /*
     * Find module.
     */
    Module module =
            moduleRepository.findById(
                    lesson.getModuleId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Module not found"));

    /*
     * Find course.
     */
    Course course =
            courseRepository.findById(
                    module.getCourseId())
                    .orElseThrow(() ->
                            new CourseNotFoundException(
                                    "Course not found"));

    /*
     * Verify ownership.
     */
    checkCourseOwnership(course);

    /*
     * Delete.
     */
    quizRepository.delete(quiz);
}
}