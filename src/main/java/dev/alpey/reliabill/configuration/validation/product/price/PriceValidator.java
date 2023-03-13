package dev.alpey.reliabill.configuration.validation.product.price;

import java.math.BigDecimal;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<Price, Double> {

    @Override
    public void initialize(Price constraintAnnotation) {
    }

    @Override
    public boolean isValid(Double price, ConstraintValidatorContext constraintValidatorContext) {
        BigDecimal decPrice = BigDecimal.valueOf(price);
        return (price >= 0 && decPrice.scale() == 2);
    }
}
