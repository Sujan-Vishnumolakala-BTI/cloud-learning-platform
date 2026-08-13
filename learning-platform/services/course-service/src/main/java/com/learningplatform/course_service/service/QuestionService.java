package com.learningplatform.course_service.service;

import com.learningplatform.course_service.dto.CreateQuestionRequest;
import com.learningplatform.course_service.dto.InternalQuizAnswerResponse;
import com.learningplatform.course_service.dto.CreateQuestionOptionRequest;
import com.learningplatform.course_service.dto.QuestionResponse;
import com.learningplatform.course_service.dto.QuestionOptionResponse;
import com.learningplatform.course_service.entity.Course;
import com.learningplatform.course_service.entity.Lesson;
import com.learningplatform.course_service.entity.Module;
import com.learningplatform.course_service.entity.Question;
import com.learningplatform.course_service.entity.QuestionOption;
import com.learningplatform.course_service.entity.Quiz;
import com.learningplatform.course_service.exception.QuestionNotFoundException;
import com.learningplatform.course_service.exception.QuestionOrderAlreadyExistsException;
import com.learningplatform.course_service.repository.CourseRepository;
import com.learningplatform.course_service.repository.ModuleRepository;
import com.learningplatform.course_service.repository.QuestionRepository;
import com.learningplatform.course_service.repository.QuestionOptionRepository;
import com.learningplatform.course_service.repository.QuizRepository;
import com.learningplatform.course_service.security.AuthenticatedUser;
// import com.learningplatform.course_service.entity.Lesson;
import com.learningplatform.course_service.repository.LessonRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository optionRepository;
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    public QuestionService(
            QuestionRepository questionRepository,
            QuestionOptionRepository optionRepository,
            QuizRepository quizRepository,
            ModuleRepository moduleRepository,
            CourseRepository courseRepository, LessonRepository lessonRepository) {

        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.quizRepository = quizRepository;
        this.moduleRepository = moduleRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    @Transactional
    public QuestionResponse createQuestion(
            Long quizId,
            CreateQuestionRequest request) {

        Quiz quiz = getQuiz(quizId);

        Course course = getCourseForQuiz(quiz);

        checkCourseOwnership(course);

        if (questionRepository
                .existsByQuizIdAndOrderIndex(
                        quizId,
                        request.getOrderIndex())) {

            throw new QuestionOrderAlreadyExistsException(
                    "A question with order "
                            + request.getOrderIndex()
                            + " already exists in this quiz");
        }

        Question question = new Question();

        question.setQuizId(quizId);
        question.setQuestionText(
                request.getQuestionText());
        question.setOrderIndex(
                request.getOrderIndex());

        Question saved = questionRepository.save(question);

        return toQuestionResponse(saved);
    }

    public List<QuestionResponse> getQuestions(
            Long quizId) {

        if (!quizRepository.existsById(quizId)) {
            throw new RuntimeException(
                    "Quiz not found with id: " + quizId);
        }

        return questionRepository
                .findByQuizIdOrderByOrderIndexAsc(quizId)
                .stream()
                .map(this::toQuestionResponse)
                .toList();
    }

    @Transactional
    public QuestionOptionResponse createOption(
            Long questionId,
            CreateQuestionOptionRequest request) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException(
                        "Question not found with id: "
                                + questionId));

        Quiz quiz = getQuiz(question.getQuizId());

        Course course = getCourseForQuiz(quiz);

        checkCourseOwnership(course);

        QuestionOption option = new QuestionOption();

        option.setQuestionId(questionId);
        option.setOptionText(
                request.getOptionText());
        option.setCorrect(
                request.isCorrect());

        QuestionOption saved = optionRepository.save(option);

        return toOptionResponse(saved);
    }

    public List<QuestionOptionResponse> getOptions(
            Long questionId) {

        if (!questionRepository.existsById(questionId)) {

            throw new QuestionNotFoundException(
                    "Question not found with id: "
                            + questionId);
        }

        return optionRepository
                .findByQuestionIdOrderByIdAsc(questionId)
                .stream()
                .map(this::toOptionResponse)
                .toList();
    }

    private Quiz getQuiz(Long quizId) {

        return quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException(
                        "Quiz not found with id: "
                                + quizId));
    }

    // private Course getCourseForQuiz(
    // Quiz quiz) {

    // var lessonId = quiz.getLessonId();

    // var lessonModule =
    // moduleRepository.findById(
    // getLessonModuleId(lessonId))
    // .orElseThrow(() ->
    // new RuntimeException(
    // "Module not found"));

    // return courseRepository
    // .findById(lessonModule.getCourseId())
    // .orElseThrow(() ->
    // new RuntimeException(
    // "Course not found"));
    // }

    private Course getCourseForQuiz(
            Quiz quiz) {

        Lesson lesson = lessonRepository.findById(
                quiz.getLessonId())
                .orElseThrow(() -> new RuntimeException(
                        "Lesson not found"));

        Module module = moduleRepository.findById(
                lesson.getModuleId())
                .orElseThrow(() -> new RuntimeException(
                        "Module not found"));

        return courseRepository
                .findById(module.getCourseId())
                .orElseThrow(() -> new RuntimeException(
                        "Course not found"));
    }

    // private Long getLessonModuleId(
    // Long lessonId) {

    // return quizRepository
    // .getEntityManager()
    // .createQuery(
    // "select l.moduleId from Lesson l " +
    // "where l.id = :id",
    // Long.class)
    // .setParameter("id", lessonId)
    // .getSingleResult();
    // }

    private void checkCourseOwnership(
            Course course) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (hasRole("ROLE_ADMIN")) {
            return;
        }

        if (!hasRole("ROLE_INSTRUCTOR")) {

            throw new AccessDeniedException(
                    "Only instructors or admins can manage questions");
        }

        if (!(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            throw new AccessDeniedException(
                    "Invalid authenticated user");
        }

        if (!course.getInstructorId()
                .equals(user.getUserId())) {

            throw new AccessDeniedException(
                    "You are not the owner of this course");
        }
    }

    private boolean hasRole(String role) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority()
                        .equals(role));
    }

    private QuestionResponse toQuestionResponse(
            Question question) {

        return new QuestionResponse(
                question.getId(),
                question.getQuizId(),
                question.getQuestionText(),
                question.getOrderIndex(),
                question.getCreatedAt(),
                question.getUpdatedAt());
    }

    private QuestionOptionResponse toOptionResponse(
            QuestionOption option) {

        return new QuestionOptionResponse(
                option.getId(),
                option.getQuestionId(),
                option.getOptionText(),
                option.isCorrect());
    }

    public InternalQuizAnswerResponse getQuizAnswers(Long quizId) {

        Quiz quiz = getQuiz(quizId);

        List<Question> questions = questionRepository
                .findByQuizIdOrderByOrderIndexAsc(
                        quizId);

        List<InternalQuizAnswerResponse.QuestionAnswerData> answers = questions.stream()
                .map(question -> {

                    List<QuestionOption> options = optionRepository
                            .findByQuestionId(
                                    question.getId());

                    QuestionOption correctOption = options.stream()
                            .filter(
                                    QuestionOption::isCorrect)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException(
                                    "No correct option configured for question "
                                            + question.getId()));

                    return new InternalQuizAnswerResponse.QuestionAnswerData(
                            question.getId(),
                            correctOption.getId());
                })
                .toList();

        return new InternalQuizAnswerResponse(
                quiz.getId(),
                quiz.getPassingScore(),
                answers);
    }
}