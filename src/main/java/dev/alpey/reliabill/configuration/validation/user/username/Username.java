package dev.alpey.reliabill.configuration.validation.user.username;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {

    String message() default "Username must contain only lower case letters and numbers, "
            + "must start with a letter, and must be 3-35 characters long!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
