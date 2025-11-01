package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import br.com.matheus161.linkai_api.dto.RegisterResponseDto;
import br.com.matheus161.linkai_api.exception.UserAlreadyExistsException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    @DisplayName("should throw an Exception when user email already exists")
    void registerCase2() {
        String email = "matheus@email.com";
        String name = "Matheus";
        String password = "Senha@123456";

        User existingUser = new User("existing-id", name, email, "hashedPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // Act
        RegisterRequestDto request = new RegisterRequestDto(name, email, password);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );

        assertEquals("User already exists", exception.getMessage());

        // Verify if user.save wasn't invoke
        verify(userRepository, never()).save(any(User.class));

        // Verify if passwordEncoder wasn't invoke
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("should throw an Exception when passwordEncoder fails")
    void registerCase3() {
        String email = "matheus@email.com";
        String name = "Matheus";
        String password = "Senha@123456";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenThrow(new RuntimeException());

        RegisterRequestDto request = new RegisterRequestDto(name, email, password);

        assertThrows(RuntimeException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("should throw an Exception when generateToken fails")
    void registerCase4() {
        String email = "matheus@email.com";
        String name = "Matheus";
        String hashedPassword =  "hashedPassword";
        String password = "Senha@123456";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(tokenService.generateToken(any(User.class))).thenThrow(new RuntimeException());

        RegisterRequestDto request = new RegisterRequestDto(name, email, password);

        assertThrows(RuntimeException.class, () -> authService.register(request));

        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenService, times(1)).generateToken(any(User.class));
    }
}