package dev.alpey.reliabill.configuration.validation.invoice.type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = DocumentTypeValdiator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentType {

    String message() default "Invalid type! Valid types: [Faktura, Profaktura]";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
