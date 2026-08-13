package com.learningplatform.user_service.security;

import com.learningplatform.user_service.entity.User;
import com.learningplatform.user_service.repository.UserRepository;
import com.learningplatform.user_service.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtService jwtService;
        private final UserRepository userRepository;

        public JwtAuthenticationFilter(
                        JwtService jwtService,
                        UserRepository userRepository) {
                this.jwtService = jwtService;
                this.userRepository = userRepository;
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest request) {

                String path = request.getServletPath();
                String method = request.getMethod();

                return method.equals(HttpMethod.POST.name())
                                && (path.equals("/api/users")
                                                || path.equals("/api/auth/login")
                                                || path.equals("/api/auth/refresh")
                                                || path.equals("/api/auth/logout"));
        }

        @Override
        protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {

                String authHeader = request.getHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        filterChain.doFilter(request, response);
                        return;
                }

                String token = authHeader.substring(7);

                try {
                        String email = jwtService.extractEmail(token);

                        User user = userRepository.findByEmail(email)
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name());

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        List.of(authority));

                        SecurityContextHolder
                                        .getContext()
                                        .setAuthentication(authentication);

                } catch (Exception e) {

                        e.printStackTrace();

                        SecurityContextHolder.clearContext();

                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");

                        response.getWriter().write("""
                                        {
                                            "status": 401,
                                            "error": "Unauthorized",
                                            "message": "Invalid or expired token"
                                        }
                                        """);
                        return;
                }

                filterChain.doFilter(request, response);
        }
}