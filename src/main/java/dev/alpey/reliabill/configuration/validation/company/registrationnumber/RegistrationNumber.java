package dev.alpey.reliabill.configuration.validation.company.registrationnumber;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = RegistrationNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RegistrationNumber {

    String message() default "Tax number contains 8 digits!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
