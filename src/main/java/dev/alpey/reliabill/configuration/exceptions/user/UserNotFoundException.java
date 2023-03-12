package dev.alpey.reliabill.configuration.exceptions.user;

import jakarta.validation.constraints.NotEmpty;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(@NotEmpty String errorMessage) {
        super(errorMessage);
    }
}
