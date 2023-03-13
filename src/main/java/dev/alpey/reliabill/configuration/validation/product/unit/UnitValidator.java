package dev.alpey.reliabill.configuration.validation.product.unit;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UnitValidator implements ConstraintValidator<Unit, String> {

    private static final int MAX_UNIT_LENGTH = 5;

    @Override
    public void initialize(Unit constraintAnnotation) {
    }

    @Override
    public boolean isValid(String unit, ConstraintValidatorContext constraintValidatorContext) {
        return unit.length() <= MAX_UNIT_LENGTH;
    }
}
