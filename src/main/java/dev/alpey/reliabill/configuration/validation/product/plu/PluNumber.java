package dev.alpey.reliabill.configuration.validation.product.plu;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PluNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PluNumber {

    String message() default "Plu must be between 1 and 99999!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
