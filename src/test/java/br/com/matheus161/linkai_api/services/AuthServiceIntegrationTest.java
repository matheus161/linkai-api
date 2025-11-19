package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.dto.LoginRequestDto;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import br.com.matheus161.linkai_api.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("should create user successfully when everything is ok")
    void registerCase1() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto("name", "email@test.com", "Senh@123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("email@test.com"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("should throw an Exception when user email already exists")
    void registerCase2() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto("name", "email@test.com", "Senh@123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    @DisplayName("should throw an Exception when name is not present")
    void registerCase3() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto(null, "email@test.com", "Senh@123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erro na validação"))
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("O nome é obrigatório"));
    }

    @Test
    @DisplayName("should throw an Exception when email is not present")
    void registerCase4() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto("name", null, "Senh@123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erro na validação"))
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("O e-mail é obrigatório"));
    }

    @Test
    @DisplayName("should throw an Exception when email is invalid")
    void registerCase5() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto("name", "invalid-email", "Senh@123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erro na validação"))
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("E-mail inválido"));
    }

    @Test
    @DisplayName("should throw an Exception when password is not present")
    void registerCase6() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto("name", "email@test.com", "senha-invalida");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erro na validação"))
                .andExpect(jsonPath("$.errors[0].field").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("Senha inválida. Deve conter " +
                        "pelo menos 8 caracteres, incluindo letra maiúscula, minúscula e número."));
    }

    @Test
    @DisplayName("should login successfully when everything is ok")
    void loginCase1() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto("name", "email@test.com", "Senh@123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        LoginRequestDto loginRequestDto = new LoginRequestDto("email@test.com",  "Senh@123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("should throw an Exception when user not exists")
    void loginCase2() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("email@test.com",  "Senh@123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.token").doesNotExist());
    }

    @Test
    @DisplayName("should throw an Exception when credential are invalid")
    void loginCase3() throws Exception {
        RegisterRequestDto requestBody = new RegisterRequestDto("name", "email@test.com", "Senh@123456");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        LoginRequestDto loginRequestDto = new LoginRequestDto("email@test.com",  "invalid-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    @DisplayName("should throw an Exception when email is not present")
    void loginCase4() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto(null,  "invalid-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erro na validação"))
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("O e-mail é obrigatório"));
    }

    @Test
    @DisplayName("should throw an Exception when passowrd is not present")
    void loginCase5() throws Exception {
        LoginRequestDto loginRequestDto = new LoginRequestDto("email@test.com",  null);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erro na validação"))
                .andExpect(jsonPath("$.errors[0].field").value("password"))
                .andExpect(jsonPath("$.errors[0].message").value("A senha é obrigatória"));
    }
}