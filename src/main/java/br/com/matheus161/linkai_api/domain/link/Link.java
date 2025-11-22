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

    @Column(nullable = false)
    private String original_link;

    @Column(unique = true)
    private String redirect_id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    public Link(String title, String description, String original_link, String redirect_id, User user) {
        this.title = title;
        this.description = description;
        this.original_link = original_link;
        this.redirect_id = redirect_id;
        this.user = user;
    }
}
