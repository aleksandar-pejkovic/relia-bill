package dev.alpey.reliabill.service.password;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.alpey.reliabill.configuration.exceptions.passwordResetToken.PasswordResetTokenExpiredException;
import dev.alpey.reliabill.configuration.exceptions.passwordResetToken.PasswordResetTokenNotFoundException;
import dev.alpey.reliabill.configuration.exceptions.user.UserNotFoundException;
import dev.alpey.reliabill.model.entity.PasswordResetToken;
import dev.alpey.reliabill.model.entity.User;
import dev.alpey.reliabill.repository.PasswordResetTokenRepository;
import dev.alpey.reliabill.repository.UserRepository;
import dev.alpey.reliabill.service.email.EmailService;

@Service
public class PasswordResetService {

    @Value("${client.url}")
    private String clientUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void resetPassword(String email) {
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
        String token = UUID.randomUUID().toString();
        String resetLink = clientUrl + "/reset-password?token=" + token;
        createPasswordResetTokenForUser(user, token);
        emailService.sendEmail(
                user.getEmail(),
                "Password reset token",
                """
                        Hello %s!
                        Please click the following link to reset your password:
                        %s
                        """.formatted(
                        user.getUsername(),
                        resetLink)
        );
    }

    private void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public void updatePassword(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository
                .findByToken(token)
                .orElseThrow(() -> new PasswordResetTokenNotFoundException("Reset token has already been used!"));

        if (passwordResetToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new PasswordResetTokenExpiredException("Reset token expired!");
        }

        User user = passwordResetToken.getUser();
        encryptUserPassword(user, newPassword);
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    private void encryptUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
    }
}
