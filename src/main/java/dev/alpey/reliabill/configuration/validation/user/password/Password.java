package dev.alpey.reliabill.configuration.validation.user.password;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "Password must contain at least one number, "
            + "one lowercase and one uppercase letter, and must be 6-12 characters long!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
