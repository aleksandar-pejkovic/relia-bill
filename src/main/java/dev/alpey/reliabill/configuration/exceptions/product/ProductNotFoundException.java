package dev.alpey.reliabill.configuration.exceptions.product;

import jakarta.validation.constraints.NotEmpty;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(@NotEmpty String errorMessage) {
        super(errorMessage);
    }
}
