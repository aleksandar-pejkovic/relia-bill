package dev.alpey.reliabill.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.User;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r ON r.name = 'ADMIN'")
    List<User> findAdmins();

    @Query("SELECT u.email FROM User u JOIN u.roles r ON r.name = 'ADMIN'")
    List<String> findAdminEmails();

    @Query("SELECT u FROM User u WHERE u.username LIKE %:searchTerm%")
    List<User> searchByUsername(@Param("searchTerm") String searchTerm);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:searchTerm%")
    List<User> searchByName(@Param("searchTerm") String searchTerm);

    @Query("SELECT u FROM User u WHERE u.email LIKE %:searchTerm%")
    List<User> searchByEmail(@Param("searchTerm") String searchTerm);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
