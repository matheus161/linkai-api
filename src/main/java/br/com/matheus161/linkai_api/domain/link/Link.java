package br.com.matheus161.linkai_api.domain.link;

import br.com.matheus161.linkai_api.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "links")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "original_link", nullable = false)
    private String originalLink;

    @Column(name = "redirect_id", unique = true)
    private String redirectId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    public Link(String title, String description, String original_link, String redirect_id, User user) {
        this.title = title;
        this.description = description;
        this.originalLink = original_link;
        this.redirectId = redirect_id;
        this.user = user;
    }
}
