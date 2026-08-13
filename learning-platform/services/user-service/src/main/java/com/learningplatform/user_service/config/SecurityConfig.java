// // package com.learningplatform.user_service.config;

// // import org.springframework.context.annotation.Bean;
// // import org.springframework.context.annotation.Configuration;
// // import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// // import org.springframework.security.crypto.password.PasswordEncoder;

// // @Configuration
// // public class SecurityConfig {

// //     @Bean
// //     public PasswordEncoder passwordEncoder() {
// //         return new BCryptPasswordEncoder();
// //     }
// // }

// package com.learningplatform.user_service.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// import com.learningplatform.user_service.security.JwtAuthenticationFilter;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// // import com.learningplatform.user_service.security.JwtAuthenticationFilter;
// import jakarta.servlet.http.HttpServletResponse;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     // @Bean
//     // public SecurityFilterChain securityFilterChain(
//     // HttpSecurity http) throws Exception {

//     // http
//     // .csrf(csrf -> csrf.disable())
//     // .authorizeHttpRequests(auth -> auth
//     // .requestMatchers("/api/auth/login").permitAll()
//     // .requestMatchers("/api/users/student-test")
//     // .hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

//     // .requestMatchers("/api/users/instructor-test")
//     // .hasAnyRole("INSTRUCTOR", "ADMIN")

//     // .requestMatchers("/api/users/admin-test")
//     // .hasRole("ADMIN")

//     // .requestMatchers("/api/users").permitAll()
//     // .requestMatchers("/api/users/**").authenticated()

//     // .anyRequest().authenticated())
//     // .addFilterBefore(
//     // jwtAuthenticationFilter,
//     // UsernamePasswordAuthenticationFilter.class);

//     // return http.build();
//     // }

//     // @Bean
//     // public SecurityFilterChain securityFilterChain(
//     // HttpSecurity http) throws Exception {

//     // http
//     // .csrf(csrf -> csrf.disable())

//     // .authorizeHttpRequests(auth -> auth
//     // .requestMatchers("/api/auth/login").permitAll()

//     // .requestMatchers("/api/users/student-test")
//     // .hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

//     // .requestMatchers("/api/users/instructor-test")
//     // .hasAnyRole("INSTRUCTOR", "ADMIN")

//     // .requestMatchers("/api/users/admin-test")
//     // .hasRole("ADMIN")

//     // .requestMatchers("/api/users").permitAll()

//     // .requestMatchers("/api/users/**")
//     // .authenticated()

//     // .anyRequest()
//     // .authenticated())

//     // .exceptionHandling(exception -> exception
//     // .authenticationEntryPoint((request, response, authException) -> {
//     // response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//     // response.setContentType("application/json");
//     // response.getWriter().write("""
//     // {
//     // "status": 401,
//     // "error": "Unauthorized",
//     // "message": "Authentication required"
//     // }
//     // """);
//     // }))

//     // .addFilterBefore(
//     // jwtAuthenticationFilter,
//     // UsernamePasswordAuthenticationFilter.class);

//     // return http.build();
//     // }

//     @Bean
//     public SecurityFilterChain securityFilterChain(
//             HttpSecurity http) throws Exception {

//         http
//                 .csrf(csrf -> csrf.disable())

//                 .sessionManagement(session -> session.sessionCreationPolicy(
//                         org.springframework.security.config.http.SessionCreationPolicy.STATELESS))

//                 .authorizeHttpRequests(auth -> auth

//                         .requestMatchers("/api/auth/login")
//                         .permitAll()

//                         .requestMatchers("/api/auth/refresh")
//                         .permitAll()

//                         .requestMatchers("/api/auth/logout")
//                         .permitAll()

//                         .requestMatchers("/api/users/student-test")
//                         .hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

//                         .requestMatchers("/api/users/instructor-test")
//                         .hasAnyRole("INSTRUCTOR", "ADMIN")

//                         .requestMatchers("/api/users/admin-test")
//                         .hasRole("ADMIN")

//                         .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
//                         // .requestMatchers(HttpMethod.GET, "/api/users/{id}").permitAll()
//                         .requestMatchers(HttpMethod.GET, "/api/users/email/**").permitAll()

//                         .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

//                         .requestMatchers("/api/users/**")
//                         .authenticated()

//                         .anyRequest()
//                         .authenticated())

//                 .exceptionHandling(exception -> exception

//                         .authenticationEntryPoint((request, response, authException) -> {
//                             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                             response.setContentType("application/json");

//                             response.getWriter().write("""
//                                     {
//                                         "status": 401,
//                                         "error": "Unauthorized",
//                                         "message": "Authentication required"
//                                     }
//                                     """);
//                         })

//                         .accessDeniedHandler((request, response, accessDeniedException) -> {
//                             response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                             response.setContentType("application/json");

//                             response.getWriter().write("""
//                                     {
//                                         "status": 403,
//                                         "error": "Forbidden",
//                                         "message": "Access denied"
//                                     }
//                                     """);
//                         }))

//                 .addFilterBefore(
//                         jwtAuthenticationFilter,
//                         UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

//     private final JwtAuthenticationFilter jwtAuthenticationFilter;

//     public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//         this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//     }
// }

package com.learningplatform.user_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.learningplatform.user_service.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        /**
         * CORS configuration.
         *
         * Angular:
         * http://127.0.0.1:4200
         *
         * Spring Boot:
         * http://127.0.0.1:8081
         */
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of("http://127.0.0.1:4200",
                                                "http://127.0.0.1:4200"));

                configuration.setAllowedMethods(
                                List.of(
                                                "GET",
                                                "POST",
                                                "PUT",
                                                "DELETE",
                                                "OPTIONS"));

                configuration.setAllowedHeaders(
                                List.of(
                                                "Authorization",
                                                "Content-Type",
                                                "Accept"));

                /*
                 * We are using JWT in the Authorization header,
                 * not cookie-based authentication.
                 */
                configuration.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                /*
                                 * We are using JWT/stateless authentication,
                                 * so CSRF protection is disabled.
                                 */
                                .csrf(csrf -> csrf.disable())

                                /*
                                 * Enable CORS using corsConfigurationSource().
                                 */
                                .cors(cors -> {
                                })

                                /*
                                 * JWT authentication is stateless.
                                 */
                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                /*
                                                 * Allow CORS preflight requests.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.OPTIONS,
                                                                "/**")
                                                .permitAll()

                                                /*
                                                 * Authentication endpoints.
                                                 */
                                                // =========================
                                                // AUTHENTICATION ENDPOINTS
                                                // =========================

                                                .requestMatchers(
                                                                "/api/auth/login",
                                                                "/api/auth/register",
                                                                "/api/auth/verify-otp",
                                                                "/api/auth/resend-otp",
                                                                "/api/auth/refresh",
                                                                "/api/auth/logout")
                                                .permitAll()
                                                /*
                                                 * Student endpoint.
                                                 */
                                                .requestMatchers(
                                                                "/api/users/student-test")
                                                .hasAnyRole(
                                                                "STUDENT",
                                                                "INSTRUCTOR",
                                                                "ADMIN")

                                                /*
                                                 * Instructor endpoint.
                                                 */
                                                .requestMatchers(
                                                                "/api/users/instructor-test")
                                                .hasAnyRole(
                                                                "INSTRUCTOR",
                                                                "ADMIN")

                                                /*
                                                 * Admin endpoint.
                                                 */

                                                .requestMatchers(
                                                                "/api/users/admin-test")
                                                .hasRole("ADMIN")

                                                /*
                                                 * Admin user APIs.
                                                 */
                                                .requestMatchers(
                                                                "/api/admin/users/**")
                                                .hasRole("ADMIN")

                                                /*
                                                 * User APIs.
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/users")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/users/email/**")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/users")
                                                .permitAll()

                                                /*
                                                 * Everything else under /api/users
                                                 * requires authentication.
                                                 */
                                                .requestMatchers(
                                                                "/api/users/**")
                                                .authenticated()

                                                .requestMatchers("/api/users/me")
                                                .authenticated()

                                                /*
                                                 * Everything else requires authentication.
                                                 */
                                                .anyRequest().authenticated())

                                /*
                                 * Return JSON for authentication failures.
                                 */
                                .exceptionHandling(exception -> exception

                                                .authenticationEntryPoint(
                                                                (request, response, authException) -> {

                                                                        response.setStatus(
                                                                                        HttpServletResponse.SC_UNAUTHORIZED);

                                                                        response.setContentType(
                                                                                        "application/json");

                                                                        response.getWriter()
                                                                                        .write("""
                                                                                                        {
                                                                                                            "status": 401,
                                                                                                            "error": "Unauthorized",
                                                                                                            "message": "Authentication required"
                                                                                                        }
                                                                                                        """);
                                                                })

                                                /*
                                                 * Return JSON for authorization failures.
                                                 */
                                                .accessDeniedHandler(
                                                                (request, response,
                                                                                accessDeniedException) -> {

                                                                        response.setStatus(
                                                                                        HttpServletResponse.SC_FORBIDDEN);

                                                                        response.setContentType(
                                                                                        "application/json");

                                                                        response.getWriter().write("""
                                                                                        {
                                                                                            "status": 403,
                                                                                            "error": "Forbidden",
                                                                                            "message": "Access denied"
                                                                                        }
                                                                                        """);
                                                                }))

                                /*
                                 * Run JWT authentication before Spring's
                                 * UsernamePasswordAuthenticationFilter.
                                 */
                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
