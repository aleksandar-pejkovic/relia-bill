package dev.alpey.reliabill.configuration.validation.product.plu;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PluNumberValidator implements ConstraintValidator<PluNumber, Integer> {

    private static final int MAX_PLU_NUMBER = 10000;

    @Override
    public void initialize(PluNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer plu, ConstraintValidatorContext constraintValidatorContext) {
        return (plu > 0 && plu < MAX_PLU_NUMBER);
    }
}
