package dev.alpey.reliabill.configuration.exceptions.company;

import jakarta.validation.constraints.NotEmpty;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(@NotEmpty String errorMessage) {
        super(errorMessage);
    }
}
