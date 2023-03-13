package dev.alpey.reliabill.configuration.validation.product.quantity;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = QuantityValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Quantity {

    String message() default "Invalid quantity";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
