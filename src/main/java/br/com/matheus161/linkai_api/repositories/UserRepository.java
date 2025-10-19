package br.com.matheus161.linkai_api.repositories;

import br.com.matheus161.linkai_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
