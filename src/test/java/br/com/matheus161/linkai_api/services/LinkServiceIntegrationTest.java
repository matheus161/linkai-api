package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.dto.CreateLinkRequestDto;
import br.com.matheus161.linkai_api.exception.GlobalExceptionHandler;
import br.com.matheus161.linkai_api.infra.security.UrlIdGeneratorService;
import br.com.matheus161.linkai_api.repositories.LinkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class LinkServiceIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlIdGeneratorService urlIdGeneratorService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private LinkRepository linkRepository;
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        linkRepository.deleteAll();
    }

    @Test
    @DisplayName("should create link successfully when everything is ok")
    void createCase1() throws Exception {
        String email = "teste@email.com";
        String token = registerAndGetToken("Teste", email, "Senha@123");

        CreateLinkRequestDto requestBody = new CreateLinkRequestDto("Link", "description",
                "original_link");
        mockMvc.perform(post("/link")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Link"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.original_link").value("original_link"))
                .andExpect(jsonPath("$.redirect_id").exists());
    }
    
    @Test
    @DisplayName("should redirect a link successfully when everything is ok")
    void redirectCase1() throws Exception {
        String email = "teste@email.com";
        String token = registerAndGetToken("Teste", email, "Senha@123");

        String originalLink = "https://example.com";
        CreateLinkRequestDto requestBody = new CreateLinkRequestDto("Link", "description", originalLink);

        String createResponseJson = mockMvc.perform(post("/link")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String redirectId = objectMapper.readTree(createResponseJson).get("redirect_id").asText();

        mockMvc.perform(get("/link/" + redirectId))
                .andDo(print())
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", originalLink));
    }

    @Test
    @DisplayName("should return 404 when link does not exist")
    void redirectCase2_notFound() throws Exception {
        String nonExistingId = "noSuchId123";

        mockMvc.perform(get("/link/" + nonExistingId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Link not found"));
    }
}
