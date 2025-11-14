package br.com.matheus161.linkai_api.controllers;

import br.com.matheus161.linkai_api.dto.LoginRequestDto;
import br.com.matheus161.linkai_api.dto.LoginResponseDto;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import br.com.matheus161.linkai_api.dto.RegisterResponseDto;
import br.com.matheus161.linkai_api.services.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService service;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestDto body) {
        LoginResponseDto response = service.login(body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequestDto body) {
        RegisterResponseDto response = service.register(body);
        return ResponseEntity.ok(response);
    }
}
