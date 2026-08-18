// package com.learningplatform.course_service.config;

// import com.learningplatform.course_service.security.JwtAuthenticationFilter;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// import org.springframework.security.config.http.SessionCreationPolicy;

// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.http.HttpMethod;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//         private final JwtAuthenticationFilter jwtAuthenticationFilter;

//         public SecurityConfig(
//                         JwtAuthenticationFilter jwtAuthenticationFilter) {

//                 this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//         }

//         @Bean
//         public SecurityFilterChain securityFilterChain(
//                         HttpSecurity http) throws Exception {

//                 http
//                                 .csrf(csrf -> csrf.disable())

//                                 .sessionManagement(session -> session.sessionCreationPolicy(
//                                                 SessionCreationPolicy.STATELESS))

//                                 .authorizeHttpRequests(auth -> auth

//                                                 // /*
//                                                 // * Temporary public endpoint.
//                                                 // * We will change course permissions
//                                                 // * when the controller is created.
//                                                 // */
//                                                 // .requestMatchers("/api/courses/public")
//                                                 // .permitAll()

//                                                 /*
//                                                  * Public endpoint used for testing
//                                                  */
//                                                 .requestMatchers("/api/courses/public")
//                                                 .permitAll()

//                                                 /*
//                                                  * Create course
//                                                  *
//                                                  * Only INSTRUCTOR or ADMIN
//                                                  */
//                                                 .requestMatchers(HttpMethod.POST, "/api/courses")
//                                                 .hasAnyRole("INSTRUCTOR", "ADMIN")

//                                                 /*
//                                                  * Update course
//                                                  *
//                                                  * Ownership is checked inside CourseService.
//                                                  */
//                                                 .requestMatchers(HttpMethod.PUT, "/api/courses/**")
//                                                 .hasAnyRole("INSTRUCTOR", "ADMIN")

//                                                 /*
//                                                  * Delete course
//                                                  */
//                                                 .requestMatchers(HttpMethod.DELETE, "/api/courses/**")
//                                                 .hasAnyRole("INSTRUCTOR", "ADMIN")

//                                                 /*
//                                                  * Read courses
//                                                  *
//                                                  * Any authenticated user.
//                                                  */
//                                                 .requestMatchers(HttpMethod.GET, "/api/courses/**")
//                                                 .authenticated()

//                                                 .requestMatchers(
//                                                                 HttpMethod.POST,
//                                                                 "/api/courses/*/publish",
//                                                                 "/api/courses/*/unpublish")
//                                                 .hasAnyRole("INSTRUCTOR", "ADMIN")

//                                                 .requestMatchers(
//                                                                 HttpMethod.POST,
//                                                                 "/api/courses/*/activate",
//                                                                 "/api/courses/*/deactivate")
//                                                 .hasRole("ADMIN")

//                                                 .requestMatchers(
//                                                                 HttpMethod.POST,
//                                                                 "/api/courses/*/modules")
//                                                 .hasAnyRole("INSTRUCTOR", "ADMIN")
//                                                 .requestMatchers(
//                                                                 HttpMethod.PUT,
//                                                                 "/api/modules/**")
//                                                 .hasAnyRole("INSTRUCTOR", "ADMIN")
//                                                 .requestMatchers(
//                                                                 HttpMethod.DELETE,
//                                                                 "/api/modules/**")
//                                                 .hasAnyRole("INSTRUCTOR", "ADMIN")
//                                                 .requestMatchers(
//                                                                 HttpMethod.GET,
//                                                                 "/api/courses/*/modules",
//                                                                 "/api/modules/**")
//                                                 .authenticated()

//                                                 .anyRequest()
//                                                 .authenticated()

//                                 // /*
//                                 // * Everyone authenticated can access
//                                 // * courses.
//                                 // */
//                                 // .requestMatchers("/api/courses/**")
//                                 // .authenticated()

//                                 // .anyRequest()
//                                 // .authenticated()
//                                 )

//                                 .addFilterBefore(
//                                                 jwtAuthenticationFilter,
//                                                 UsernamePasswordAuthenticationFilter.class);

//                 return http.build();
//         }
// }

package com.learningplatform.course_service.config;

import com.learningplatform.course_service.security.JwtAuthenticationFilter;

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
                                // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                /*
                                                 * PUBLIC
                                                 */
                                                .requestMatchers(
                                                                "/api/courses/public",
                                                                "/actuator/health")
                                                .permitAll()

                                                /*
                                                 * ==========================
                                                 * MODULES
                                                 * ==========================
                                                 */

                                                /*
                                                 * Create Module
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/courses/*/modules")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * Read Modules
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/courses/*/modules",
                                                                "/api/modules/**")
                                                .authenticated()

                                                /*
                                                 * Update Module
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/modules/**")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * Delete Module
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/modules/**")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * LESSON CREATE
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/modules/*/lessons")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * LESSON READ
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/lessons/*/video/stream")
                                                .hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/modules/*/lessons",
                                                                "/api/lessons/**")
                                                .authenticated()

                                                /*
                                                 * LESSON UPDATE
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/lessons/**")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * LESSON DELETE
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/lessons/**")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * ==========================
                                                 * COURSES
                                                 * ==========================
                                                 */

                                                /*
                                                 * Create Course
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/courses")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * Publish / Unpublish
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/courses/*/publish",
                                                                "/api/courses/*/unpublish")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * Activate / Deactivate
                                                 *
                                                 * ADMIN ONLY
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/courses/*/activate",
                                                                "/api/courses/*/deactivate")
                                                .hasRole("ADMIN")

                                                /*
                                                 * Update Course
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/courses/**")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * Delete Course
                                                 */
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/courses/**")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
                                                 * Read Courses
                                                 */
                                                // .requestMatchers(
                                                // HttpMethod.GET,
                                                // "/api/courses/**")
                                                // .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/courses/**")
                                                .hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/lessons/*/quiz")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/quizzes/*/questions",
                                                                "/api/questions/*/options")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/quizzes/*/questions",
                                                                "/api/questions/*/options")
                                                .authenticated()

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/lessons/*/course")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "INSTRUCTOR",
                                                                "STUDENT")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/courses/*/lesson-count")
                                                .hasAnyRole(
                                                                "ADMIN",
                                                                "INSTRUCTOR",
                                                                "STUDENT")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/instructor/courses")
                                                .hasRole("INSTRUCTOR")
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/instructor/**")
                                                .hasAnyRole("ADMIN", "INSTRUCTOR")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/internal/courses/*/students",
                                                                "/api/internal/courses/*/progress")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/courses/*/lessons")
                                                .hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/instructor/quizzes/*/results")
                                                .hasAnyRole("INSTRUCTOR", "ADMIN")

                                                /*
 * LESSON CREATE
 */
.requestMatchers(
        HttpMethod.POST,
        "/api/modules/*/lessons")
.hasAnyRole("INSTRUCTOR", "ADMIN")

/*
 * VIDEO UPLOAD URL
 */
.requestMatchers(
        HttpMethod.POST,
        "/api/lessons/*/video/upload-url")
.hasAnyRole("INSTRUCTOR", "ADMIN")

/*
 * VIDEO UPLOAD COMPLETE
 */
.requestMatchers(
        HttpMethod.POST,
        "/api/lessons/*/video/complete")
.hasAnyRole("INSTRUCTOR", "ADMIN")

/*
 * LESSON READ
 */
.requestMatchers(
        HttpMethod.GET,
        "/api/lessons/*/video/stream")
.hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")

.requestMatchers(
        HttpMethod.GET,
        "/api/modules/*/lessons",
        "/api/lessons/**")
.authenticated()

/*
 * LESSON UPDATE
 */
.requestMatchers(
        HttpMethod.PUT,
        "/api/lessons/**")
.hasAnyRole("INSTRUCTOR", "ADMIN")

/*
 * LESSON DELETE
 */
.requestMatchers(
        HttpMethod.DELETE,
        "/api/lessons/**")
.hasAnyRole("INSTRUCTOR", "ADMIN")
                                                /*
                                                 * Everything else
                                                 */
                                                .anyRequest()
                                                .authenticated())

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // @Bean
        // public CorsConfigurationSource corsConfigurationSource() {

        // CorsConfiguration configuration = new CorsConfiguration();

        // configuration.setAllowedOrigins(
        // List.of(
        // "http://127.0.0.1:4200",
        // "http://localhost:4200"));

        // configuration.setAllowedMethods(
        // List.of(
        // "GET",
        // "POST",
        // "PUT",
        // "DELETE",
        // "PATCH",
        // "OPTIONS"));

        // configuration.setAllowedHeaders(
        // List.of(
        // "Authorization",
        // "Content-Type",
        // "Accept",
        // "Origin"));

        // configuration.setExposedHeaders(
        // List.of(
        // "Authorization"));

        // configuration.setAllowCredentials(true);

        // UrlBasedCorsConfigurationSource source = new
        // UrlBasedCorsConfigurationSource();

        // source.registerCorsConfiguration(
        // "/**",
        // configuration);

        // return source;
        // }
}