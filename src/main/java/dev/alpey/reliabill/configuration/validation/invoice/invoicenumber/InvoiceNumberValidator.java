package dev.alpey.reliabill.configuration.validation.invoice.invoicenumber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class InvoiceNumberValidator implements ConstraintValidator<InvoiceNumber, String> {

    @Override
    public void initialize(InvoiceNumber invoiceNumber) {
    }

    @Override
    public boolean isValid(String invoiceNumber, ConstraintValidatorContext context) {
        String regex = "^[a-zA-Z0-9/-]+${2,30}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(invoiceNumber);
        return matcher.find();
    }
}
