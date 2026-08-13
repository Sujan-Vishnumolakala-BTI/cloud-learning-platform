// package com.learningplatform.enroll_service.repository;

// import com.learningplatform.enroll_service.entity.QuizAttempt;
// import com.learningplatform.enroll_service.entity.QuizAttemptStatus;

// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.List;
// import java.util.Optional;

// public interface QuizAttemptRepository
//         extends JpaRepository<QuizAttempt, Long> {

//     List<QuizAttempt> findByUserIdOrderByStartedAtDesc(
//             Long userId);

//     List<QuizAttempt> findByUserIdAndQuizIdOrderByStartedAtDesc(
//             Long userId,
//             Long quizId);

//     Optional<QuizAttempt> findByIdAndUserId(
//             Long id,
//             Long userId);

//     boolean existsByUserIdAndQuizIdAndStatus(
//             Long userId,
//             Long quizId,
//             QuizAttemptStatus status);

//     // List<QuizAttempt> findByQuizIdOrderByStartedAtDesc(Long quizId);
// }

package com.learningplatform.enroll_service.repository;

import com.learningplatform.enroll_service.entity.QuizAttempt;
import com.learningplatform.enroll_service.entity.QuizAttemptStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository
        extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt>
    findByUserIdOrderByStartedAtDesc(
            Long userId);

    List<QuizAttempt>
    findByUserIdAndQuizIdOrderByStartedAtDesc(
            Long userId,
            Long quizId);

    List<QuizAttempt>
    findByQuizIdOrderByStartedAtDesc(
            Long quizId);

    Optional<QuizAttempt>
    findByIdAndUserId(
            Long id,
            Long userId);

    boolean existsByUserIdAndQuizIdAndStatus(
            Long userId,
            Long quizId,
            QuizAttemptStatus status);
}