package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import br.com.matheus161.linkai_api.dto.RegisterResponseDto;
import br.com.matheus161.linkai_api.infra.security.TokenService;
import br.com.matheus161.linkai_api.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void login() {
    }

    @Test
    @DisplayName("should create user successfully when everything is ok")
    void registerCase1() throws Exception {
        String email = "matheus@email.com";
        String name = "Matheus";
        String password = "Senha@123456";
        String hashedPassword = "hashedPassword";
        String token = "token";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(tokenService.generateToken(any(User.class))).thenReturn(token);

        // Act
        RegisterRequestDto request = new RegisterRequestDto(name, email, password);
        RegisterResponseDto response = authService.register(request);

        // Assert
        assertEquals(email, response.email());
        assertEquals(token, response.token());

        verify(userRepository, times(1)).save(argThat(user ->
                        user.getName().equals(name) &&
                        user.getEmail().equals(email) &&
                        user.getPassword().equals(hashedPassword)
        ));

        verify(tokenService, times(1)).generateToken(any(User.class));
    }

    @Test
    @DisplayName("should throw and Exception when user email already exists")
    void registerCase2() {
    }

    @Test
    @DisplayName("should throw and Exception when user email already exists")
    void registerCase3() {
    }
}