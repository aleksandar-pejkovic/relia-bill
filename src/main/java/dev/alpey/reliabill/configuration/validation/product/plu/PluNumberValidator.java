package dev.alpey.reliabill.configuration.validation.product.plu;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PluNumberValidator implements ConstraintValidator<PluNumber, Integer> {

    @Override
    public void initialize(PluNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer plu, ConstraintValidatorContext constraintValidatorContext) {
        return (plu > 0 && plu < 10000);
    }
}
