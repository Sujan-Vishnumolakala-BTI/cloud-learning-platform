package com.learningplatform.enroll_service.service;

import com.learningplatform.enroll_service.dto.CertificateResponse;
import com.learningplatform.enroll_service.entity.Certificate;
import com.learningplatform.enroll_service.entity.Enrollment;
import com.learningplatform.enroll_service.entity.EnrollmentStatus;
import com.learningplatform.enroll_service.repository.CertificateRepository;
import com.learningplatform.enroll_service.repository.EnrollmentRepository;
import com.learningplatform.enroll_service.security.AuthenticatedUser;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseClient courseClient;

    public CertificateService(
            CertificateRepository certificateRepository,
            EnrollmentRepository enrollmentRepository,
            CourseClient courseClient) {

        this.certificateRepository = certificateRepository;

        this.enrollmentRepository = enrollmentRepository;

        this.courseClient = courseClient;
    }

    @Transactional
    public CertificateResponse generateCertificate(
            Long courseId) {

        Long userId = getCurrentUserId();

        Enrollment enrollment = enrollmentRepository
                .findByUserIdAndCourseId(
                        userId,
                        courseId)
                .orElseThrow(() -> new AccessDeniedException(
                        "You are not enrolled in this course"));

        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {

            throw new AccessDeniedException(
                    "Course must be completed before generating a certificate");
        }

        /*
         * Return existing certificate.
         */
        return certificateRepository
                .findByUserIdAndCourseId(
                        userId,
                        courseId)
                .map(this::toResponse)
                .orElseGet(() -> createCertificate(
                        userId,
                        courseId));
    }

    private CertificateResponse createCertificate(
            Long userId,
            Long courseId) {

        CourseClient.CourseData course = courseClient.getCourse(courseId);
        if (course == null) {

            throw new RuntimeException(
                    "Course not found");
        }

        Certificate certificate = new Certificate();

        certificate.setCertificateNumber(
                generateCertificateNumber());

        certificate.setUserId(userId);

        certificate.setCourseId(courseId);

        certificate.setCourseTitle(
                course.title());

        certificate.setStudentName(
                getCurrentUserEmail());

        Certificate saved = certificateRepository.save(
                certificate);

        return toResponse(saved);
    }

    private String generateCertificateNumber() {

        return "CERT-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    public CertificateResponse getMyCertificate(
            Long courseId) {

        Long userId = getCurrentUserId();

        Certificate certificate = certificateRepository
                .findByUserIdAndCourseId(
                        userId,
                        courseId)
                .orElseThrow(() -> new RuntimeException(
                        "Certificate not found"));

        return toResponse(certificate);
    }

    private CertificateResponse toResponse(
            Certificate certificate) {

        return new CertificateResponse(
                certificate.getId(),
                certificate.getCertificateNumber(),
                certificate.getUserId(),
                certificate.getCourseId(),
                certificate.getCourseTitle(),
                certificate.getStudentName(),
                certificate.getIssuedAt());
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

    private String getCurrentUserEmail() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {

            throw new AccessDeniedException(
                    "User is not authenticated");
        }

        return user.getEmail();
    }

    /*
     * Adjust this DTO to match your existing
     * Course Service response if necessary.
     */
    
}