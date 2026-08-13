package com.learningplatform.enroll_service.repository;

import com.learningplatform.enroll_service.entity.Certificate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    Optional<Certificate> findByUserIdAndCourseId(
            Long userId,
            Long courseId);

    boolean existsByUserIdAndCourseId(
            Long userId,
            Long courseId);

    Optional<Certificate> findByCertificateNumber(
            String certificateNumber);
}