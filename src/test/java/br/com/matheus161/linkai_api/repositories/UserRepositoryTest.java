package br.com.matheus161.linkai_api.repositories;

import br.com.matheus161.linkai_api.domain.user.User;
import br.com.matheus161.linkai_api.dto.RegisterRequestDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void cleanDatabase() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("Should get user successfully from DB")
    void findByEmailCase1() {
        String email = "matheus@email.com";
        RegisterRequestDto data = new RegisterRequestDto("Matheus", email, "Senh@132456");
        this.createUser(data);

        Optional<User> existingUser = this.userRepository.findByEmail(email);

        assertThat(existingUser.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Should not get user from DB when user not exists")
    void findByEmailCase2() {
        String email = "matheus@email.com";

        Optional<User> existingUser = this.userRepository.findByEmail(email);

        assertThat(existingUser.isEmpty()).isTrue();
    }

    private User createUser(RegisterRequestDto data) {
        User user = new User(data.name(), data.email(), data.password());
        this.entityManager.persist(user);
        return user;
    }
}