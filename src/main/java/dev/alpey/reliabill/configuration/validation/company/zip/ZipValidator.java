package dev.alpey.reliabill.configuration.validation.company.zip;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ZipValidator implements ConstraintValidator<Zip, String> {

    @Override
    public void initialize(Zip constraintAnnotation) {
    }

    @Override
    public boolean isValid(String zip, ConstraintValidatorContext context) {
        String regex = "^\\d{5}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(zip);
        return matcher.find();
    }
}
