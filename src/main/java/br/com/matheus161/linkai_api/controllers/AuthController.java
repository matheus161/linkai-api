package br.com.matheus161.linkai_api.controllers;

import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.LoginRequestDto;
import br.com.matheus161.linkai_api.dto.LoginResponseDto;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import br.com.matheus161.linkai_api.dto.RegisterResponseDto;
import br.com.matheus161.linkai_api.infra.security.TokenService;
import br.com.matheus161.linkai_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDto body) {
        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(body.password(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(new LoginResponseDto(user.getName(), token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDto body) {
        Optional<User> user = this.repository.findByEmail(body.email());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        String password = passwordEncoder.encode(body.password());
        User newUser = new User(body.name(), body.email(), password);
        this.repository.save(newUser);

        String token = tokenService.generateToken(newUser);
        return ResponseEntity.ok(new RegisterResponseDto(newUser.getName(), token));
    }
}
