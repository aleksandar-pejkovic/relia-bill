package dev.alpey.reliabill.configuration.validation.product.taxrate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxRateValidator implements ConstraintValidator<TaxRate, Integer> {

    @Override
    public void initialize(TaxRate constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer taxRate, ConstraintValidatorContext constraintValidatorContext) {
        return (taxRate == 0
                || taxRate == 10
                || taxRate == 20);
    }
}
