package dev.alpey.reliabill.configuration.validation.company.registrationnumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RegistrationNumberValidator implements ConstraintValidator<RegistrationNumber, String> {

    @Override
    public void initialize(RegistrationNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String registrationNumber, ConstraintValidatorContext context) {
        String regex = "^\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(registrationNumber);
        return matcher.find();
    }
}
