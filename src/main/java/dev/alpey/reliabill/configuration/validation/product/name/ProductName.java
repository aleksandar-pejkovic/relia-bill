package dev.alpey.reliabill.configuration.validation.product.name;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.alpey.reliabill.configuration.validation.product.unit.UnitValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UnitValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ProductName {

    String message() default "Product name invalid!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
