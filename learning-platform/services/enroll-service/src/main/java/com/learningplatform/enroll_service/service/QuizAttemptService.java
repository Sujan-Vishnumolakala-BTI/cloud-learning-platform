package com.learningplatform.enroll_service.service;

import com.learningplatform.enroll_service.dto.QuizAnswerRequest;
import com.learningplatform.enroll_service.dto.QuizAttemptResponse;
import com.learningplatform.enroll_service.dto.StartQuizAttemptRequest;
import com.learningplatform.enroll_service.dto.SubmitQuizRequest;
import com.learningplatform.enroll_service.entity.Enrollment;
import com.learningplatform.enroll_service.entity.EnrollmentStatus;
import com.learningplatform.enroll_service.entity.QuizAnswer;
import com.learningplatform.enroll_service.entity.QuizAttempt;
import com.learningplatform.enroll_service.entity.QuizAttemptStatus;
import com.learningplatform.enroll_service.repository.EnrollmentRepository;
import com.learningplatform.enroll_service.repository.QuizAnswerRepository;
import com.learningplatform.enroll_service.repository.QuizAttemptRepository;
import com.learningplatform.enroll_service.security.AuthenticatedUser;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class QuizAttemptService {

    private final QuizAttemptRepository attemptRepository;
    private final QuizAnswerRepository answerRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;

    public QuizAttemptService(
            QuizAttemptRepository attemptRepository,
            QuizAnswerRepository answerRepository,
            EnrollmentRepository enrollmentRepository,
            CourseClient courseClient) {

        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.courseClient = courseClient;
    }

    @Transactional
    public QuizAttemptResponse startAttempt(
            StartQuizAttemptRequest request) {

        Long userId = getCurrentUserId();

        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(
                        userId,
                        getCourseIdForQuiz(
                                request.getQuizId()))
                .orElseThrow(() -> new AccessDeniedException(
                        "You are not enrolled in this course"));

        if (enrollment.getStatus() != EnrollmentStatus.ACTIVE) {

            throw new AccessDeniedException(
                    "Your enrollment is not active");
        }

        QuizAttempt attempt = new QuizAttempt();

        attempt.setUserId(userId);
        attempt.setQuizId(request.getQuizId());
        attempt.setStatus(
                QuizAttemptStatus.IN_PROGRESS);

        QuizAttempt saved = attemptRepository.save(attempt);

        return toResponse(saved);
    }

    @Transactional
    public QuizAttemptResponse submitAttempt(
            Long attemptId,
            SubmitQuizRequest request) {

        Long userId = getCurrentUserId();

        QuizAttempt attempt = attemptRepository
                .findByIdAndUserId(
                        attemptId,
                        userId)
                .orElseThrow(() -> new RuntimeException(
                        "Quiz attempt not found"));

        if (attempt.getStatus() == QuizAttemptStatus.COMPLETED) {

            throw new RuntimeException(
                    "Quiz attempt has already been submitted");
        }

        CourseClient.QuizAnswerData quizData = courseClient.getQuizAnswers(
                attempt.getQuizId());

        Map<Long, Long> correctAnswers = quizData.questions()
                .stream()
                .collect(
                        Collectors.toMap(
                                CourseClient.QuestionAnswerData::questionId,
                                CourseClient.QuestionAnswerData::correctOptionId));

        int correctCount = 0;

        for (QuizAnswerRequest requestAnswer : request.getAnswers()) {

            Long correctOption = correctAnswers.get(
                    requestAnswer.getQuestionId());

            if (correctOption == null) {

                throw new RuntimeException(
                        "Invalid question ID: "
                                + requestAnswer.getQuestionId());
            }

            boolean correct = correctOption.equals(
                    requestAnswer.getOptionId());

            if (correct) {
                correctCount++;
            }

            QuizAnswer answer = new QuizAnswer();

            answer.setAttemptId(attemptId);
            answer.setQuestionId(
                    requestAnswer.getQuestionId());
            answer.setOptionId(
                    requestAnswer.getOptionId());
            answer.setCorrect(correct);

            answerRepository.save(answer);
        }

        int totalQuestions = correctAnswers.size();

        int score = totalQuestions == 0
                ? 0
                : (correctCount * 100)
                        / totalQuestions;

        boolean passed = score >= quizData.passingScore();

        attempt.setStatus(
                QuizAttemptStatus.COMPLETED);

        attempt.setSubmittedAt(
                LocalDateTime.now());

        attempt.setTotalQuestions(
                totalQuestions);

        attempt.setCorrectAnswers(
                correctCount);

        attempt.setScore(score);

        attempt.setPassed(passed);

        QuizAttempt saved = attemptRepository.save(attempt);

        return toResponse(saved);
    }

    public List<QuizAttemptResponse> getMyAttempts(Long quizId) {

        Long userId = getCurrentUserId();

        return attemptRepository
                .findByUserIdAndQuizIdOrderByStartedAtDesc(
                        userId,
                        quizId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<QuizAttemptResponse> getQuizResults(Long quizId) {

        return attemptRepository
                .findByQuizIdOrderByStartedAtDesc(
                        quizId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private Long getCurrentUserId() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            throw new AccessDeniedException(
                    "User is not authenticated");
        }

        return user.getUserId();
    }

    /*
     * Temporary implementation.
     *
     * We need the quiz -> lesson -> module -> course
     * relationship from Course Service to determine
     * which course the student must be enrolled in.
     */
    private Long getCourseIdForQuiz(Long quizId) {

        CourseClient.QuizData quiz = courseClient.getQuiz(quizId);

        return courseClient.getCourseIdForQuiz(
                quiz.lessonId());
    }

    private QuizAttemptResponse toResponse(
            QuizAttempt attempt) {

        return new QuizAttemptResponse(
                attempt.getId(),
                attempt.getUserId(),
                attempt.getQuizId(),
                attempt.getStatus(),
                attempt.getStartedAt(),
                attempt.getSubmittedAt(),
                attempt.getTotalQuestions(),
                attempt.getCorrectAnswers(),
                attempt.getScore(),
                attempt.getPassed());
    }
}