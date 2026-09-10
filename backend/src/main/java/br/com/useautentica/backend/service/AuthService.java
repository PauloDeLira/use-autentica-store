package br.com.useautentica.backend.service;

import br.com.useautentica.backend.dto.auth.LoginRequest;
import br.com.useautentica.backend.dto.auth.LoginResponse;
import br.com.useautentica.backend.entity.User;
import br.com.useautentica.backend.repository.UserRepository;
import br.com.useautentica.backend.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email()).orElseThrow();
        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }
}
