package dev.alpey.reliabill.configuration.validation.company.zip;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = ZipValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Zip {

    String message() default "Zip contains 5 digits!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
