package dev.alpey.reliabill.configuration.validation.product.description;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DescriptionValidator implements ConstraintValidator<Description, String> {

    @Override
    public void initialize(Description description) {
    }

    @Override
    public boolean isValid(String about, ConstraintValidatorContext context) {
        if (about == null) {
            return false;
        }
        String regex = "^[a-zA-Z0-9ČĆŠĐŽčćšđž,\\.!\\? \\u0027-]+${1,300}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(about);
        return matcher.find();
    }
}
