package br.com.matheus161.linkai_api.repositories;

import br.com.matheus161.linkai_api.domain.link.Link;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends JpaRepository<Link, UUID> {
    Optional<Link> findByTitleOrOriginalLink(String title, String originalLink);
}
