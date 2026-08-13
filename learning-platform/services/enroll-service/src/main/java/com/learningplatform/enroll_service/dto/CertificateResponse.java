package com.learningplatform.enroll_service.dto;

import java.time.LocalDateTime;

public class CertificateResponse {

    private Long id;
    private String certificateNumber;
    private Long userId;
    private Long courseId;
    private String courseTitle;
    private String studentName;
    private LocalDateTime issuedAt;

    public CertificateResponse(
            Long id,
            String certificateNumber,
            Long userId,
            Long courseId,
            String courseTitle,
            String studentName,
            LocalDateTime issuedAt) {

        this.id = id;
        this.certificateNumber = certificateNumber;
        this.userId = userId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.studentName = studentName;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getStudentName() {
        return studentName;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }
}