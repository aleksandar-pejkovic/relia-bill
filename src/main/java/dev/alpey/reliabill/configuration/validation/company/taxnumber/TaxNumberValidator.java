package dev.alpey.reliabill.configuration.validation.company.taxnumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaxNumberValidator implements ConstraintValidator<TaxNumber, String> {

    @Override
    public void initialize(TaxNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String taxNumber, ConstraintValidatorContext context) {
        String regex = "^\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(taxNumber);
        return matcher.find();
    }
}
