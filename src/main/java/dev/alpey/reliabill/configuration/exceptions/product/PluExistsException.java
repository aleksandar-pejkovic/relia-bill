package dev.alpey.reliabill.configuration.exceptions.product;

import java.sql.SQLIntegrityConstraintViolationException;

import jakarta.validation.constraints.NotEmpty;

public class PluExistsException extends SQLIntegrityConstraintViolationException {
    public PluExistsException(@NotEmpty String errorMessage) {
        super(errorMessage);
    }
}
