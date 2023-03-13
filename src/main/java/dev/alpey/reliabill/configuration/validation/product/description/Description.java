package dev.alpey.reliabill.configuration.validation.product.description;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DescriptionValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {

    String message() default "Incorrect format!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
