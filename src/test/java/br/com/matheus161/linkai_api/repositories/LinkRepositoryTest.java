package br.com.matheus161.linkai_api.repositories;

import br.com.matheus161.linkai_api.domain.link.Link;
import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.CreateLinkRequestDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class LinkRepositoryTest {
    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private EntityManager entityManager;

    final String title = "Link test";
    final String description = "This is a test link";
    final String original_link = "Original link";
    final String redirect_id = "redirect-id";

    @Test
    @DisplayName("Should get link by title or original link successfully from DB")
    void findByTitleOrOriginalLinkSuccess() {
        User user = createUser();

        CreateLinkRequestDto data = new CreateLinkRequestDto(title, description, original_link, user.getId());
        createLink(data, user);

        Optional<Link> existingLink = linkRepository.findByTitleOrOriginalLink(title, original_link);

        assertThat(existingLink).isPresent();
        assertThat(existingLink.get().getTitle()).isEqualTo(title);
        assertThat(existingLink.get().getOriginalLink()).isEqualTo(original_link);
    }

    private User createUser() {
        User user = new User("Matheus", "email@email.com", "Senh@132456");
        entityManager.persist(user);
        entityManager.flush(); // garante persistência no BD
        return user;
    }

    private Link createLink(CreateLinkRequestDto data, User user) {
        Link link = new Link(data.title(), data.description(), data.original_link(), redirect_id, user);
        entityManager.persist(link);
        entityManager.flush(); // garante persistência no BD antes da consulta
        return link;
    }
}
