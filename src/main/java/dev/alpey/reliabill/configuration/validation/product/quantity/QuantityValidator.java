package dev.alpey.reliabill.configuration.validation.product.quantity;

import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class QuantityValidator implements ConstraintValidator<Quantity, Double> {

    private static final int ALLOWED_DECIMAL_SCALE = 3;

    private static final double BIGGEST_PRICE = 100000000.00;

    @Override
    public void initialize(Quantity constraintAnnotation) {
    }

    @Override
    public boolean isValid(Double quantity, ConstraintValidatorContext constraintValidatorContext) {
        BigDecimal decPrice = BigDecimal.valueOf(quantity);
        return (quantity > 0 && decPrice.scale() <= ALLOWED_DECIMAL_SCALE && quantity < BIGGEST_PRICE);
    }
}
