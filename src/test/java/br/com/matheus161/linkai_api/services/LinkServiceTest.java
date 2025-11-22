package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.CreateLinkRequestDto;
import br.com.matheus161.linkai_api.dto.CreateLinkResponseDto;
import br.com.matheus161.linkai_api.infra.security.UrlIdGeneratorService;
import br.com.matheus161.linkai_api.repositories.LinkRepository;
import br.com.matheus161.linkai_api.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class LinkServiceTest {
    @Mock
    private LinkRepository linkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UrlIdGeneratorService urlIdGeneratorService;

    @InjectMocks
    private LinkService linkService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private final String title = "Link test";
    private final String description = "This is a test link";
    private final String original_link = "Original link";
    private final String redirect_id = "redirect-id";
    private final String user_id = "valid-user-id";

    @Test
    @DisplayName("should create a link successfully when everything is ok")
    void createCase1() {
        User user = new User("name", "email", "Senha@123456");
        when(userRepository.findById(user_id)).thenReturn(Optional.of(user));
        when(linkRepository.findByTitleOrOriginalLink(title, original_link)).thenReturn(Optional.empty());
        when(urlIdGeneratorService.generate()).thenReturn(redirect_id);


        // Act
        CreateLinkRequestDto request = new CreateLinkRequestDto(title, description, original_link, user_id);
        CreateLinkResponseDto response = linkService.create(request);

        System.out.println(response);

        // Assert
        assertEquals(title, response.title());
        assertEquals(description, response.description());
        assertEquals(original_link, response.original_link());
        assertEquals(redirect_id, response.redirect_id());

        verify(linkRepository, times(1)).save(argThat(link ->
                link.getTitle().equals(title) &&
                        link.getDescription().equals(description) &&
                        link.getOriginal_link().equals(original_link) &&
                        link.getRedirect_id().equals(redirect_id)
        ));
    }
}
