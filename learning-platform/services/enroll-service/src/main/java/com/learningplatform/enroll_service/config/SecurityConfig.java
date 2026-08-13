package com.learningplatform.enroll_service.config;

import com.learningplatform.enroll_service.security.JwtAuthenticationFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http) throws Exception {

                http
                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // =========================
                                                // ACTUATOR HEALTH
                                                // =========================
                                                .requestMatchers(
                                                                "/actuator/health",
                                                                "/actuator/health/**")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/api/admin/enrollments/**")
                                                .hasRole("ADMIN")

                                                // =========================
                                                // ENROLLMENT
                                                // =========================
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/enrollments")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/enrollments/my")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/enrollments/**")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/enrollments/**")
                                                .authenticated()

                                                .requestMatchers(
                                                                "/api/progress/**")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/quiz-attempts",
                                                                "/api/quiz-attempts/*/submit")
                                                .authenticated()

                                                // Student certificate endpoints
                                                .requestMatchers(
                                                                "/api/certificates/my",
                                                                "/api/certificates/*/download")
                                                .hasRole("STUDENT")

                                                .requestMatchers(
                                                                "/api/certificates/courses/**")
                                                .hasRole("STUDENT")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/quiz-attempts/quiz/*/my")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/internal/courses/*/students",
                                                                "/api/internal/courses/*/progress",
                                                                "/api/internal/quizzes/*/results")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                .anyRequest()
                                                .authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOrigins(
                                List.of(
                                                "http://127.0.0.1:4200",
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
                                                "Accept",
                                                "Origin"));

                configuration.setExposedHeaders(
                                List.of(
                                                "Authorization"));

                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration(
                                "/**",
                                configuration);

                return source;
        }

}