// package com.learningplatform.course_service.security;

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

//         String authHeader = request.getHeader("Authorization");

//         System.out.println(
//                 "COURSE JWT FILTER: "
//                         + request.getRequestURI());

//         if (authHeader == null
//                 || !authHeader.startsWith("Bearer ")) {

//             filterChain.doFilter(request, response);
//             return;
//         }

//         String token = authHeader.substring(7);

//         try {

//             if (!jwtService.isTokenValid(token)) {

//                 filterChain.doFilter(request, response);
//                 return;
//             }

//             Claims claims = jwtService.extractAllClaims(token);

//             String email = claims.getSubject();

//             String role = claims.get("role", String.class);

//             Long userId = claims.get("userId", Long.class);

//             System.out.println(
//                     "COURSE USER: " + email);

//             System.out.println(
//                     "COURSE ROLE: " + role);

//             System.out.println(
//                     "COURSE USER ID: " + userId);

//             if (role == null) {

//                 filterChain.doFilter(
//                         request,
//                         response);

//                 return;
//             }

//             String authority = "ROLE_" + role;

//             AuthenticatedUser authenticatedUser = new AuthenticatedUser(
//                     userId,
//                     email);

//             UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                     authenticatedUser,
//                     null,
//                     List.of(
//                             new SimpleGrantedAuthority(
//                                     authority)));

//             // UsernamePasswordAuthenticationToken authentication = new
//             // UsernamePasswordAuthenticationToken(
//             // email,
//             // null,
//             // List.of(
//             // new SimpleGrantedAuthority(
//             // authority)));

//             SecurityContextHolder
//                     .getContext()
//                     .setAuthentication(authentication);

//         } catch (Exception e) {

//             System.out.println(
//                     "JWT ERROR: " + e.getMessage());

//             SecurityContextHolder
//                     .clearContext();
//         }

//         filterChain.doFilter(request, response);
//     }
// }

package com.learningplatform.course_service.security;

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

        String authHeader =
                request.getHeader("Authorization");

        System.out.println(
                "========== COURSE JWT FILTER ==========");

        System.out.println(
                "REQUEST: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI());

        System.out.println(
                "AUTHORIZATION HEADER PRESENT: "
                        + (authHeader != null));

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            System.out.println(
                    "JWT: NO BEARER TOKEN");

            filterChain.doFilter(
                    request,
                    response);

            return;
        }

        String token =
                authHeader.substring(7);

        System.out.println(
                "JWT: BEARER TOKEN FOUND");

        try {

            boolean valid =
                    jwtService.isTokenValid(token);

            System.out.println(
                    "JWT VALID: " + valid);

            if (!valid) {

                System.out.println(
                        "JWT: TOKEN INVALID");

                filterChain.doFilter(
                        request,
                        response);

                return;
            }

            Claims claims =
                    jwtService.extractAllClaims(token);

            System.out.println(
                    "JWT CLAIMS: " + claims);

            String email =
                    claims.getSubject();

            String role =
                    claims.get("role", String.class);

            Long userId =
                    claims.get("userId", Long.class);

            System.out.println(
                    "COURSE USER: " + email);

            System.out.println(
                    "COURSE ROLE: " + role);

            System.out.println(
                    "COURSE USER ID: " + userId);

            if (role == null) {

                System.out.println(
                        "JWT ERROR: ROLE IS NULL");

                filterChain.doFilter(
                        request,
                        response);

                return;
            }

            if (userId == null) {

                System.out.println(
                        "JWT ERROR: USER ID IS NULL");

                filterChain.doFilter(
                        request,
                        response);

                return;
            }

            String authority =
                    "ROLE_" + role.toUpperCase();

            System.out.println(
                    "SPRING AUTHORITY: "
                            + authority);

            AuthenticatedUser authenticatedUser =
                    new AuthenticatedUser(
                            userId,
                            email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
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
                    "========================================");

        } catch (Exception e) {

            System.out.println(
                    "========== JWT ERROR ==========");

            System.out.println(
                    "ERROR TYPE: "
                            + e.getClass().getName());

            System.out.println(
                    "ERROR MESSAGE: "
                            + e.getMessage());

            e.printStackTrace();

            SecurityContextHolder
                    .clearContext();

            System.out.println(
                    "================================");
        }

        filterChain.doFilter(
                request,
                response);
    }
}