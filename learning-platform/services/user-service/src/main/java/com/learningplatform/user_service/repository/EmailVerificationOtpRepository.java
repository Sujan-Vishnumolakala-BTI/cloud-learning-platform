package com.learningplatform.user_service.repository;

import com.learningplatform.user_service.entity.EmailVerificationOtp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EmailVerificationOtpRepository
        extends JpaRepository<EmailVerificationOtp, Long> {

    Optional<EmailVerificationOtp>
    findTopByEmailAndUsedFalseOrderByCreatedAtDesc(
            String email);

     @Modifying
    @Transactional
    void deleteByEmail(String email);
}