package com.dulceshop.backend.config;

import com.dulceshop.backend.repository.UserRepository;
import com.dulceshop.backend.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            String token = header.substring(BEARER_PREFIX.length());
            try {
                String email = jwtService.extractEmail(token);
                userRepository.findByEmail(email)
                        .filter(user -> user.isActive())
                        .ifPresent(user -> {
                            var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    user.getEmail(), null, List.of(authority));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            } catch (JwtException ex) {
                log.warn("Invalid JWT rejected: {}", ex.getMessage());
            }
        }

        chain.doFilter(request, response);
    }
}