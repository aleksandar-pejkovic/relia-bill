package dev.alpey.reliabill.configuration.validation.product.name;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductNameValidator implements ConstraintValidator<ProductName, String> {

    @Override
    public void initialize(ProductName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String productName, ConstraintValidatorContext constraintValidatorContext) {
        if (productName == null) {
            return false;
        }
        String regex = "^[a-zA-Z0-9\\s-]+${2,50}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(productName);
        return matcher.find();
    }
}
