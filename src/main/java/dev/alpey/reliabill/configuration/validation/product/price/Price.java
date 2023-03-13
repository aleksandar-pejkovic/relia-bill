package dev.alpey.reliabill.configuration.validation.product.price;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PriceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Price {

    String message() default "Invalid price";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
