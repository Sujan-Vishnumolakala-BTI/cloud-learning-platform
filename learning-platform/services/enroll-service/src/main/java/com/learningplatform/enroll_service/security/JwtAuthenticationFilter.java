// package com.learningplatform.enroll_service.security;
// import io.jsonwebtoken.Claims;
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import java.io.IOException;
// import java.util.List;

// @Component
// public class JwtAuthenticationFilter
//         extends OncePerRequestFilter {

//     private final JwtService jwtService;

//     public JwtAuthenticationFilter(
//             JwtService jwtService) {

//         this.jwtService = jwtService;
//     }

//     @Override
//     protected void doFilterInternal(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             FilterChain filterChain)
//             throws ServletException, IOException {

//         String authHeader =
//                 request.getHeader("Authorization");

//         System.out.println(
//                 "ENROLLMENT JWT FILTER: "
//                         + request.getRequestURI());

//         /*
//          * No JWT supplied.
//          *
//          * This is okay for public endpoints
//          * such as /actuator/health.
//          */
//         if (authHeader == null
//                 || !authHeader.startsWith("Bearer ")) {

//             filterChain.doFilter(
//                     request,
//                     response);

//             return;
//         }

//         String token =
//                 authHeader.substring(7);

//         try {

//             /*
//              * Validate JWT
//              */
//             if (!jwtService.isTokenValid(token)) {

//                 System.out.println(
//                         "ENROLLMENT JWT: Invalid token");

//                 filterChain.doFilter(
//                         request,
//                         response);

//                 return;
//             }

//             /*
//              * Extract claims
//              */
//             Claims claims =
//                     jwtService.extractAllClaims(token);

//             String email =
//                     claims.getSubject();

//             String role =
//                     claims.get(
//                             "role",
//                             String.class);

//             Long userId =
//                     claims.get(
//                             "userId",
//                             Long.class);

//             System.out.println(
//                     "ENROLLMENT USER: "
//                             + email);

//             System.out.println(
//                     "ENROLLMENT ROLE: "
//                             + role);

//             System.out.println(
//                     "ENROLLMENT USER ID: "
//                             + userId);

//             /*
//              * Required claims
//              */
//             if (email == null
//                     || role == null
//                     || userId == null) {

//                 System.out.println(
//                         "ENROLLMENT JWT: Required claims missing");

//                 filterChain.doFilter(
//                         request,
//                         response);

//                 return;
//             }

//             /*
//              * Convert role to Spring Security authority
//              *
//              * STUDENT -> ROLE_STUDENT
//              * INSTRUCTOR -> ROLE_INSTRUCTOR
//              * ADMIN -> ROLE_ADMIN
//              */
//             String authority =
//                     "ROLE_" + role;

//             /*
//              * Create authenticated principal
//              */
//             AuthenticatedUser authenticatedUser =
//                     new AuthenticatedUser(
//                             userId,
//                             email,
//                             role);

//             /*
//              * Create Spring Authentication
//              */
//             UsernamePasswordAuthenticationToken authentication =
//                     new UsernamePasswordAuthenticationToken(
//                             authenticatedUser,
//                             null,
//                             List.of(
//                                     new SimpleGrantedAuthority(
//                                             authority)));

//             /*
//              * Store authentication
//              * in SecurityContext
//              */
//             SecurityContextHolder
//                     .getContext()
//                     .setAuthentication(
//                             authentication);

//             System.out.println(
//                     "ENROLLMENT AUTHENTICATION SUCCESS");

//         } catch (Exception e) {

//             System.out.println(
//                     "ENROLLMENT JWT ERROR: "
//                             + e.getMessage());

//             SecurityContextHolder
//                     .clearContext();
//         }

//         /*
//          * Continue request
//          */
//         filterChain.doFilter(
//                 request,
//                 response);
//     }
// }

package com.learningplatform.enroll_service.security;

import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(
            JwtService jwtService) {

        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        System.out.println(
                "\n========== ENROLLMENT JWT FILTER ==========");

        System.out.println(
                "Request: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI());

        System.out.println(
                "Authorization header present: "
                        + (authHeader != null));

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "JWT RESULT: NO BEARER TOKEN");

            filterChain.doFilter(
                    request,
                    response);

            return;
        }

        String token = authHeader.substring(7);

        try {

            System.out.println(
                    "JWT RESULT: Bearer token found");

            boolean valid = jwtService.isTokenValid(token);

            System.out.println(
                    "JWT VALID: " + valid);

            if (!valid) {

                System.out.println(
                        "JWT RESULT: INVALID TOKEN");

                filterChain.doFilter(
                        request,
                        response);

                return;
            }

            Claims claims = jwtService.extractAllClaims(token);

            String email = claims.getSubject();

            String role = claims.get(
                    "role",
                    String.class);

            Long userId = claims.get(
                    "userId",
                    Long.class);

            System.out.println(
                    "JWT EMAIL: " + email);

            System.out.println(
                    "JWT ROLE: " + role);

            System.out.println(
                    "JWT USER ID: " + userId);

            if (email == null ||
                    role == null ||
                    userId == null) {

                System.out.println(
                        "JWT RESULT: REQUIRED CLAIM MISSING");

                filterChain.doFilter(
                        request,
                        response);

                return;
            }

            String authority = "ROLE_" + role;

            AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                    userId,
                    email,
                    role);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    null,
                    List.of(
                            new SimpleGrantedAuthority(
                                    authority)));

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(
                            authentication);

            System.out.println(
                    "SPRING AUTHENTICATION: SUCCESS");

            System.out.println(
                    "AUTHORITY: " + authority);

        } catch (Exception e) {

            System.out.println(
                    "JWT EXCEPTION TYPE: "
                            + e.getClass().getName());

            System.out.println(
                    "JWT EXCEPTION: "
                            + e.getMessage());

            SecurityContextHolder
                    .clearContext();
        }

        filterChain.doFilter(
                request,
                response);
    }
}