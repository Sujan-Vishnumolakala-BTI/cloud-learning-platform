// package com.learningplatform.user_service.controller;

// import com.learningplatform.user_service.dto.LoginRequest;
// import com.learningplatform.user_service.dto.LoginResponse;
// import com.learningplatform.user_service.service.AuthService;

// import jakarta.validation.Valid;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/auth")
// public class AuthController {

//     private final AuthService authService;

//     public AuthController(AuthService authService) {
//         this.authService = authService;
//     }

//     @PostMapping("/login")
//     public ResponseEntity<LoginResponse> login(
//             @Valid @RequestBody LoginRequest request) {

//         return ResponseEntity.ok(
//                 authService.login(request)
//         );
//     }
// }

package com.learningplatform.user_service.controller;

import com.learningplatform.user_service.dto.LoginRequest;
import com.learningplatform.user_service.dto.LoginResponse;
import com.learningplatform.user_service.dto.RefreshTokenRequest;
import com.learningplatform.user_service.service.AuthService;
import com.learningplatform.user_service.dto.RegisterRequest;
import com.learningplatform.user_service.dto.RegisterResponse;
import com.learningplatform.user_service.dto.VerifyOtpRequest;
import com.learningplatform.user_service.dto.ResendOtpRequest;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                authService.refreshToken(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ResponseEntity
                .status(201)
                .body(
                        authService.register(request));
    }
    @PostMapping("/verify-otp")
public ResponseEntity<?> verifyOtp(
        @Valid @RequestBody VerifyOtpRequest request) {

    return ResponseEntity.ok(
            java.util.Map.of(
                    "status", 200,
                    "message",
                    authService.verifyOtp(request)
            )
    );
}
@PostMapping("/resend-otp")
public ResponseEntity<?> resendOtp(
        @Valid @RequestBody ResendOtpRequest request) {

    return ResponseEntity.ok(
            java.util.Map.of(
                    "status", 200,
                    "message",
                    authService.resendOtp(request)
            )
    );
}

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Valid @RequestBody RefreshTokenRequest request) {

        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(
                java.util.Map.of(
                        "status", 200,
                        "message", "Logout successful"));
    }

}