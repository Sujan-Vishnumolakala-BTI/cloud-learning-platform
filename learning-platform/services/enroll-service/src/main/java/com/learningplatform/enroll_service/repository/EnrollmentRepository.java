package com.learningplatform.enroll_service.repository;

import com.learningplatform.enroll_service.entity.Enrollment;
import com.learningplatform.enroll_service.entity.EnrollmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository
                extends JpaRepository<Enrollment, Long> {

        Optional<Enrollment> findByUserIdAndCourseId(
                        Long userId,
                        Long courseId);

        List<Enrollment> findByUserIdOrderByEnrolledAtDesc(
                        Long userId);

        List<Enrollment> findByUserIdAndStatus(
                        Long userId,
                        EnrollmentStatus status);

        boolean existsByUserIdAndCourseId(
                        Long userId,
                        Long courseId);

        boolean existsByUserIdAndCourseIdAndStatus(
                        Long userId,
                        Long courseId,
                        EnrollmentStatus status);

        List<Enrollment> findByUserId(Long userId);

        List<Enrollment> findByCourseId(
                        Long courseId);

        List<Enrollment> findByCourseIdAndStatus(
                        Long courseId,
                        EnrollmentStatus status);

        long countByStatus(EnrollmentStatus status);

        long countByCourseIdAndStatus(
                        Long courseId,
                        EnrollmentStatus status);

        long countByUserIdAndStatus(
                        Long userId,
                        EnrollmentStatus status);

}