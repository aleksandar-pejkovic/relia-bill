package dev.alpey.reliabill.configuration.validation.product.price;

import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<Price, Double> {

    private static final int ALLOWED_DECIMAL_SCALE = 2;

    private static final double BIGGEST_PRICE = 100000000.00;

    @Override
    public void initialize(Price constraintAnnotation) {
    }

    @Override
    public boolean isValid(Double price, ConstraintValidatorContext constraintValidatorContext) {
        BigDecimal decPrice = BigDecimal.valueOf(price);
        return (price > 0 && decPrice.scale() == ALLOWED_DECIMAL_SCALE && price < BIGGEST_PRICE);
    }
}
