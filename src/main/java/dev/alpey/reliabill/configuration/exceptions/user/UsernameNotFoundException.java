package dev.alpey.reliabill.configuration.exceptions.user;

import jakarta.validation.constraints.NotEmpty;

public class UsernameNotFoundException extends RuntimeException {
    public UsernameNotFoundException(@NotEmpty String errorMessage) {
        super(errorMessage);
    }
}
