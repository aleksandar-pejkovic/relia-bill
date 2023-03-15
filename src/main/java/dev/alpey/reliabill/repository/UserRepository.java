package dev.alpey.reliabill.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.User;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r ON r.name = 'ADMIN'")
    List<User> findAdmins();

    @Query("SELECT u.email FROM User u JOIN u.roles r ON r.name = 'ADMIN'")
    List<String> findAdminEmails();

    List<User> searchByUsername(String searchTerm);

    List<User> searchByName(String searchTerm);

    List<User> searchByEmail(String searchTerm);

    boolean existsByUsername(String username);
}
