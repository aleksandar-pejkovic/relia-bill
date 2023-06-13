package dev.alpey.reliabill.repository;

import java.util.Optional;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import dev.alpey.reliabill.model.entity.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends ListCrudRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
}
