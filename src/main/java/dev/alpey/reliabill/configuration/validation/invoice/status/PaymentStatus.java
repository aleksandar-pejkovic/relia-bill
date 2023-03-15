package dev.alpey.reliabill.configuration.validation.invoice.status;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PaymentStatusValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PaymentStatus {

    String message() default "Invalid status! Valid status: [PENDING, PARTIALY_PAID, PAID]";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
