package com.dulceshop.backend.service;
import com.dulceshop.backend.dto.RegisterRequest;
import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.EmailAlreadyUsedException;
import com.dulceshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dulceshop.backend.dto.AuthResponse;
import com.dulceshop.backend.dto.LoginRequest;
import com.dulceshop.backend.exception.InvalidCredentialsException;
@Service
@RequiredArgsConstructor 
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Transactional
    public User register(RegisterRequest request){
        String email=request.email().trim().toLowerCase();
        if(userRepository.existsByEmail(email)){
            throw new EmailAlreadyUsedException(email);
        }
        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRole(Role.CUSTOMER);
        return userRepository.save(user);

    }
        @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .filter(User::isActive)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponse(token, "Bearer", user.getEmail(), user.getRole());
    }
    
}
