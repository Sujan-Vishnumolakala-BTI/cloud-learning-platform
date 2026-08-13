// package com.learningplatform.user_service.service;

// import com.learningplatform.user_service.dto.LoginRequest;
// import com.learningplatform.user_service.dto.LoginResponse;
// import com.learningplatform.user_service.entity.User;
// import com.learningplatform.user_service.exception.AccountDisabledException;
// import com.learningplatform.user_service.exception.AccountLockedException;
// import com.learningplatform.user_service.exception.InvalidCredentialsException;
// import com.learningplatform.user_service.repository.RefreshTokenRepository;
// import com.learningplatform.user_service.repository.UserRepository;

// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// @Service
// public class AuthService {

//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;
//     private final JwtService jwtService;
//     private final RefreshTokenRepository refreshTokenRepository;

//     public AuthService(
//             UserRepository userRepository,
//             PasswordEncoder passwordEncoder,
//             JwtService jwtService,
//             RefreshTokenRepository refreshTokenRepository) {

//         this.userRepository = userRepository;
//         this.passwordEncoder = passwordEncoder;
//         this.jwtService = jwtService;
//         this.refreshTokenRepository = refreshTokenRepository;
//     }

//     public LoginResponse login(LoginRequest request) {

//         // 1. Find user
//         User user = userRepository.findByEmail(request.getEmail())
//                 .orElseThrow(() -> new InvalidCredentialsException(
//                         "Invalid email or password"));

//         // 2. Check whether account is enabled
//         if (!user.isEnabled()) {
//             throw new AccountDisabledException(
//                     "Account is disabled");
//         }

//         // 3. Check whether account is locked
//         if (user.isAccountLocked()) {
//             throw new AccountLockedException(
//                     "Account is locked");
//         }

//         // 4. Check password
//         if (!passwordEncoder.matches(
//                 request.getPassword(),
//                 user.getPassword())) {

//             int attempts = user.getFailedLoginAttempts() + 1;

//             user.setFailedLoginAttempts(attempts);

//             // Lock account after 5 failed attempts
//             if (attempts >= 5) {
//                 user.setAccountLocked(true);
//             }

//             userRepository.save(user);

//             throw new InvalidCredentialsException(
//                     "Invalid email or password");
//         }

//         // 5. Successful login
//         user.setFailedLoginAttempts(0);

//         userRepository.save(user);

//         // 6. Generate JWT
//         String token = jwtService.generateToken(user);

//         return new LoginResponse(token);
//     }
// }

package com.learningplatform.user_service.service;

import com.learningplatform.user_service.dto.LoginRequest;
import com.learningplatform.user_service.dto.LoginResponse;
import com.learningplatform.user_service.dto.RefreshTokenRequest;
import com.learningplatform.user_service.entity.RefreshToken;
import com.learningplatform.user_service.entity.User;
import com.learningplatform.user_service.exception.AccountDisabledException;
import com.learningplatform.user_service.exception.AccountLockedException;
import com.learningplatform.user_service.exception.InvalidCredentialsException;
import com.learningplatform.user_service.repository.UserRepository;

import com.learningplatform.user_service.dto.RegisterRequest;
import com.learningplatform.user_service.dto.RegisterResponse;
import com.learningplatform.user_service.dto.VerifyOtpRequest;
import com.learningplatform.user_service.dto.ResendOtpRequest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final RefreshTokenService refreshTokenService;
        private final OtpService otpService;
        private final EmailService emailService;

        public AuthService(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        RefreshTokenService refreshTokenService,
                        OtpService otpService,
                        EmailService emailService) {

                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.refreshTokenService = refreshTokenService;
                this.otpService = otpService;
                this.emailService = emailService;
        }

        public LoginResponse login(LoginRequest request) {

                User user = userRepository
                                .findByEmail(request.getEmail())
                                .orElseThrow(() -> new InvalidCredentialsException(
                                                "Invalid email or password"));

                if (!user.isEnabled()) {

                        throw new AccountDisabledException(
                                        "Account is disabled");
                }

                if (user.isAccountLocked()) {

                        throw new AccountLockedException(
                                        "Account is locked");
                }

                if (!passwordEncoder.matches(
                                request.getPassword(),
                                user.getPassword())) {

                        int attempts = user.getFailedLoginAttempts() + 1;

                        user.setFailedLoginAttempts(attempts);

                        if (attempts >= 5) {

                                user.setAccountLocked(true);
                        }

                        userRepository.save(user);

                        throw new InvalidCredentialsException(
                                        "Invalid email or password");
                }

                // Successful login
                user.setFailedLoginAttempts(0);

                userRepository.save(user);

                String accessToken = jwtService.generateToken(user);

                String refreshToken = refreshTokenService.createRefreshToken(user);

                return new LoginResponse(
                                accessToken,
                                refreshToken);
        }

        public LoginResponse refreshToken(
                        RefreshTokenRequest request) {

                RefreshToken refreshToken = refreshTokenService.validateRefreshToken(
                                request.getRefreshToken());

                User user = refreshToken.getUser();

                if (!user.isEnabled()) {

                        throw new AccountDisabledException(
                                        "Account is disabled");
                }

                /*
                 * Rotate refresh token.
                 *
                 * Old token becomes invalid.
                 */
                refreshTokenService.revokeToken(
                                request.getRefreshToken());

                String newAccessToken = jwtService.generateToken(user);

                String newRefreshToken = refreshTokenService.createRefreshToken(user);

                return new LoginResponse(
                                newAccessToken,
                                newRefreshToken);
        }

        public RegisterResponse register(
                        RegisterRequest request) {

                String email = request.getEmail()
                                .trim()
                                .toLowerCase();

                if (userRepository.existsByEmail(email)) {

                        throw new RuntimeException(
                                        "Email already registered");
                }

                User user = new User();

                user.setFirstName(
                                request.getFirstName().trim());

                user.setLastName(
                                request.getLastName() == null
                                                ? null
                                                : request.getLastName().trim());

                user.setEmail(email);

                user.setPassword(
                                passwordEncoder.encode(
                                                request.getPassword()));

                user.setRole(
                                com.learningplatform.user_service.entity.Role.STUDENT);

                /*
                 * User cannot login until OTP
                 * verification succeeds.
                 */
                user.setEnabled(false);

                userRepository.save(user);

                String otp = otpService.generateOtp(email);

                emailService.sendOtp(
                                email,
                                otp);

                return new RegisterResponse(
                                email,
                                "Registration successful. OTP sent to your email.");
        }

        public String verifyOtp(
                        VerifyOtpRequest request) {

                String email = request.getEmail()
                                .trim()
                                .toLowerCase();

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found"));

                if (user.isEnabled()) {

                        throw new RuntimeException(
                                        "Email is already verified");
                }

                boolean valid = otpService.verifyOtp(
                                email,
                                request.getOtp());

                if (!valid) {

                        throw new RuntimeException(
                                        "Invalid or expired OTP");
                }

                user.setEnabled(true);

                userRepository.save(user);

                return "Email verified successfully. You can now login.";
        }

        public String resendOtp(
                        ResendOtpRequest request) {

                String email = request.getEmail()
                                .trim()
                                .toLowerCase();

                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found"));

                if (user.isEnabled()) {

                        throw new RuntimeException(
                                        "Email is already verified");
                }

                String otp = otpService.generateOtp(email);

                emailService.sendOtp(
                                email,
                                otp);

                return "A new OTP has been sent to your email.";
        }

        public void logout(String refreshToken) {

                refreshTokenService.revokeToken(refreshToken);
        }
}