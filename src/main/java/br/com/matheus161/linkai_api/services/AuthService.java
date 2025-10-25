package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.LoginRequestDto;
import br.com.matheus161.linkai_api.dto.LoginResponseDto;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import br.com.matheus161.linkai_api.dto.RegisterResponseDto;
import br.com.matheus161.linkai_api.exception.UserAlreadyExistsException;
import br.com.matheus161.linkai_api.infra.security.TokenService;
import br.com.matheus161.linkai_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;


    @Override
    public LoginResponseDto login(LoginRequestDto body) {
        User user = repository.findByEmail(body.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = tokenService.generateToken(user);
        return new LoginResponseDto(user.getName(), token);
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto body) {
        Optional<User> existingUser = repository.findByEmail(body.email());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(body.password());
        User newUser = new User(body.name(), body.email(), hashedPassword);
        repository.save(newUser);

        String token = tokenService.generateToken(newUser);
        return new RegisterResponseDto(newUser.getName(), token);
    }
}
