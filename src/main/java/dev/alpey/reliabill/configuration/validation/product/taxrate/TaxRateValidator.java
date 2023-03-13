package dev.alpey.reliabill.configuration.validation.product.taxrate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxRateValidator implements ConstraintValidator<TaxRate, Integer> {

    private static final int TAX_RATE_ZERO = 0;
    private static final int TAX_RATE_TEN = 10;
    private static final int TAX_RATE_TWENTY = 20;

    @Override
    public void initialize(TaxRate constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer taxRate, ConstraintValidatorContext constraintValidatorContext) {
        return (taxRate == TAX_RATE_ZERO
                || taxRate == TAX_RATE_TEN
                || taxRate == TAX_RATE_TWENTY);
    }
}
