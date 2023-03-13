package dev.alpey.reliabill.configuration.validation.company.taxnumber;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TaxNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaxNumber {

    String message() default "Tax number contains 9 digits!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
