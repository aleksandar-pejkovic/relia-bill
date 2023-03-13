package dev.alpey.reliabill.configuration.validation.product.taxrate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TaxRateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TaxRate {

    String message() default "Tax rate must be 0, 10 or 20!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
