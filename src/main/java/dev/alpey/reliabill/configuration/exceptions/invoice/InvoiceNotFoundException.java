package dev.alpey.reliabill.configuration.exceptions.invoice;

import jakarta.validation.constraints.NotEmpty;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(@NotEmpty String errorMessage) {
        super(errorMessage);
    }
}
