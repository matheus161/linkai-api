package br.com.matheus161.linkai_api.services;

import br.com.matheus161.linkai_api.domain.link.Link;
import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.CreateLinkRequestDto;
import br.com.matheus161.linkai_api.dto.CreateLinkResponseDto;
import br.com.matheus161.linkai_api.exception.LinkAlreadyExistsException;
import br.com.matheus161.linkai_api.exception.LinkNotFoundException;
import br.com.matheus161.linkai_api.exception.UserNotFoundException;
import br.com.matheus161.linkai_api.infra.security.UrlIdGeneratorService;
import br.com.matheus161.linkai_api.repositories.LinkRepository;
import br.com.matheus161.linkai_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LinkService implements ILinkService {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final UrlIdGeneratorService urlIdGeneratorService;


    @Override
    public CreateLinkResponseDto create(CreateLinkRequestDto body, String userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Link> existingLink = linkRepository.findByOriginalLinkAndUserId(body.title(), body.original_link());
        if (existingLink.isPresent()) {
            throw new LinkAlreadyExistsException("Link already exists");
        }

        String uniqueUrlId = urlIdGeneratorService.generate();
        Link newLink = new Link(body.title(), body.description(), body.original_link(), uniqueUrlId, existingUser);
        linkRepository.save(newLink);

        return new CreateLinkResponseDto(
                newLink.getTitle(),
                newLink.getDescription(),
                newLink.getOriginalLink(),
                newLink.getRedirectId());
    }

    @Override
    public String findOriginalLink(String redirectId) {
        Link existingLink = linkRepository.findLinkByRedirectId(redirectId)
                .orElseThrow(() -> new LinkNotFoundException("Link not found"));

        return existingLink.getOriginalLink();
    }

}
