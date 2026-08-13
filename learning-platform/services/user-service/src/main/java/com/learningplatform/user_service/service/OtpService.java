package com.learningplatform.user_service.service;

import com.learningplatform.user_service.entity.EmailVerificationOtp;
import com.learningplatform.user_service.repository.EmailVerificationOtpRepository;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private final EmailVerificationOtpRepository otpRepository;

    private final Random random = new Random();

    public OtpService(
            EmailVerificationOtpRepository otpRepository) {

        this.otpRepository = otpRepository;
    }

    @Transactional
    public String generateOtp(String email) {

        otpRepository.deleteByEmail(email);

        String otp =
                String.format(
                        "%06d",
                        random.nextInt(1_000_000));

        EmailVerificationOtp verificationOtp =
                new EmailVerificationOtp();

        verificationOtp.setEmail(email);

        verificationOtp.setOtp(otp);

        verificationOtp.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(10));

        verificationOtp.setUsed(false);

        otpRepository.save(verificationOtp);

        return otp;
    }

    @Transactional
    public boolean verifyOtp(
            String email,
            String otp) {

        EmailVerificationOtp verificationOtp =
                otpRepository
                        .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(
                                email)
                        .orElse(null);

        if (verificationOtp == null) {
            return false;
        }

        if (
            verificationOtp
                .getExpiresAt()
                .isBefore(LocalDateTime.now())
        ) {
            return false;
        }

        if (
            !verificationOtp
                .getOtp()
                .equals(otp)
        ) {
            return false;
        }

        verificationOtp.setUsed(true);

        otpRepository.save(
                verificationOtp);

        return true;
    }
}