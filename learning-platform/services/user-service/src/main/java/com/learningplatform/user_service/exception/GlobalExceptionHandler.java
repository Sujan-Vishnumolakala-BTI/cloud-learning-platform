// // package com.learningplatform.user_service.exception;

// // import org.springframework.http.HttpStatus;
// // import org.springframework.http.ResponseEntity;
// // import org.springframework.web.bind.MethodArgumentNotValidException;
// // import org.springframework.web.bind.annotation.ExceptionHandler;
// // import org.springframework.web.bind.annotation.RestControllerAdvice;

// // import java.util.HashMap;
// // import java.util.Map;

// // @RestControllerAdvice
// // public class GlobalExceptionHandler {

// //     @ExceptionHandler(MethodArgumentNotValidException.class)
// //     public ResponseEntity<Map<String, String>> handleValidationErrors(
// //             MethodArgumentNotValidException ex) {

// //         Map<String, String> errors = new HashMap<>();

// //         ex.getBindingResult()
// //                 .getFieldErrors()
// //                 .forEach(error -> errors.put(
// //                         error.getField(),
// //                         error.getDefaultMessage()));

// //         return ResponseEntity
// //                 .status(HttpStatus.BAD_REQUEST)
// //                 .body(errors);
// //     }

// //     @ExceptionHandler(EmailAlreadyExistsException.class)
// //     public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(
// //             EmailAlreadyExistsException ex) {

// //         Map<String, String> error = new HashMap<>();

// //         error.put("error", ex.getMessage());

// //         return ResponseEntity
// //                 .status(HttpStatus.CONFLICT)
// //                 .body(error);
// //     }

// //     @ExceptionHandler(RoleChangeNotAllowedException.class)
// //     public ResponseEntity<Map<String, String>> handleRoleChangeNotAllowed(
// //             RoleChangeNotAllowedException ex) {

// //         Map<String, String> error = new HashMap<>();

// //         error.put("error", ex.getMessage());

// //         return ResponseEntity
// //                 .status(HttpStatus.FORBIDDEN)
// //                 .body(error);
// //     }
// // }

// package com.learningplatform.user_service.exception;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.MethodArgumentNotValidException;
// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.RestControllerAdvice;

// import java.util.HashMap;
// import java.util.Map;

// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     public ResponseEntity<Map<String, String>> handleValidationErrors(
//             MethodArgumentNotValidException ex) {

//         Map<String, String> errors = new HashMap<>();

//         ex.getBindingResult()
//                 .getFieldErrors()
//                 .forEach(error ->
//                         errors.put(
//                                 error.getField(),
//                                 error.getDefaultMessage()
//                         )
//                 );

//         return ResponseEntity
//                 .status(HttpStatus.BAD_REQUEST)
//                 .body(errors);
//     }

//     @ExceptionHandler(EmailAlreadyExistsException.class)
//     public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(
//             EmailAlreadyExistsException ex) {

//         Map<String, String> error = new HashMap<>();

//         error.put("status", "409");
//         error.put("error", "Conflict");
//         error.put("message", ex.getMessage());

//         return ResponseEntity
//                 .status(HttpStatus.CONFLICT)
//                 .body(error);
//     }

//     @ExceptionHandler(RuntimeException.class)
//     public ResponseEntity<Map<String, String>> handleRuntimeException(
//             RuntimeException ex) {

//         Map<String, String> error = new HashMap<>();

//         error.put("status", "400");
//         error.put("error", "Bad Request");
//         error.put("message", ex.getMessage());

//         return ResponseEntity
//                 .status(HttpStatus.BAD_REQUEST)
//                 .body(error);
//     }
// } 

package com.learningplatform.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // 400 - Validation errors
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationErrors(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(errors);
        }

        // 409 - Email already exists
        @ExceptionHandler(EmailAlreadyExistsException.class)
        public ResponseEntity<Map<String, String>> handleEmailAlreadyExists(
                        EmailAlreadyExistsException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "409");
                error.put("error", "Conflict");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        // 403 - Role change not allowed
        @ExceptionHandler(RoleChangeNotAllowedException.class)
        public ResponseEntity<Map<String, String>> handleRoleChangeNotAllowed(
                        RoleChangeNotAllowedException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "403");
                error.put("error", "Forbidden");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(error);
        }

        // 404 - User not found
        @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<Map<String, String>> handleUserNotFound(
                        UserNotFoundException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "404");
                error.put("error", "Not Found");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(error);
        }

        // 500 - Unexpected errors
        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleGeneralException(
                        Exception ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "500");
                error.put("error", "Internal Server Error");
                error.put("message", "An unexpected error occurred");

                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(error);
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<Map<String, String>> handleInvalidCredentials(
                        InvalidCredentialsException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "401");
                error.put("error", "Unauthorized");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(error);
        }

        @ExceptionHandler(AccountDisabledException.class)
        public ResponseEntity<Map<String, String>> handleAccountDisabled(
                        AccountDisabledException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "403");
                error.put("error", "Forbidden");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(error);
        }

        @ExceptionHandler(AccountLockedException.class)
        public ResponseEntity<Map<String, String>> handleAccountLocked(
                        AccountLockedException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "403");
                error.put("error", "Forbidden");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(error);
        }

        @ExceptionHandler(InvalidRefreshTokenException.class)
        public ResponseEntity<Map<String, String>> handleInvalidRefreshToken(
                        InvalidRefreshTokenException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "401");
                error.put("error", "Unauthorized");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(error);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Map<String, String>> handleAccessDenied(
                        AccessDeniedException ex) {

                Map<String, String> error = new HashMap<>();

                error.put("status", "403");
                error.put("error", "Forbidden");
                error.put("message", ex.getMessage());

                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(error);
        }
}
