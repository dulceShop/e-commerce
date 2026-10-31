package com.dulceshop.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.dulceshop.backend.repository.UserRepository;
import com.dulceshop.backend.service.JwtService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.dulceshop.backend.repository.UserRepository;
import com.dulceshop.backend.service.JwtService;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

        @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserRepository userRepository) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/products/**", "/api/categories/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/seller/**").hasAnyRole("SELLER", "ADMIN")
                    .anyRequest().authenticated())
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepository),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}