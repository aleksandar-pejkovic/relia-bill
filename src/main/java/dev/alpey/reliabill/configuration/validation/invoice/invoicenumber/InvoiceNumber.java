package dev.alpey.reliabill.configuration.validation.invoice.invoicenumber;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = InvoiceNumberValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InvoiceNumber {

    String message() default "Invalid invoice number format!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
