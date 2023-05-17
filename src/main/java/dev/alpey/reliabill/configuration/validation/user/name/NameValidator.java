package dev.alpey.reliabill.configuration.validation.user.name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<Name, String> {

    @Override
    public void initialize(Name name) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        if (name == null) {
            return false;
        }
        String regex = "^[A-ZČĆŠĐŽ][a-zA-ZčćšđžČĆŠĐŽ]*([ -][A-ZČĆŠĐŽ][a-zA-ZčćšđžČĆŠĐŽ]*){1,49}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }
}
