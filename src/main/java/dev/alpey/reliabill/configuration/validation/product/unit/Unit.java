package dev.alpey.reliabill.configuration.validation.product.unit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UnitValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unit {

    String message() default "Max 5 characters!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
