package dev.alpey.reliabill.configuration.validation.company.bankaccount;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BankAccountValidator implements ConstraintValidator<BankAccount, String> {

    @Override
    public void initialize(BankAccount constraintAnnotation) {
    }

    @Override
    public boolean isValid(String bankAccount, ConstraintValidatorContext context) {
        String regex = "^(?:\\d{3})-(?:0*(\\d{1,13})|\\1)-(\\d{2})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(bankAccount);
        return matcher.find();
    }
}
