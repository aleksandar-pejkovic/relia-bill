package dev.alpey.reliabill.configuration.validation.company.website;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class WebsiteValidator implements ConstraintValidator<Website, String> {

    @Override
    public void initialize(Website constraintAnnotation) {
    }

    @Override
    public boolean isValid(String website, ConstraintValidatorContext context) {
        String regex = "^(https?://)?(www\\.)?[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}(/[a-zA-Z0-9-]+)*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(website);
        return matcher.find();
    }
}
