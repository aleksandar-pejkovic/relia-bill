package dev.alpey.reliabill.configuration.validation.invoice.status;

import dev.alpey.reliabill.enums.InvoiceStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PaymentStatusValidator implements ConstraintValidator<PaymentStatus, String> {

    @Override
    public void initialize(PaymentStatus paymentStatus) {
    }

    @Override
    public boolean isValid(String paymentStatus, ConstraintValidatorContext context) {
        return InvoiceStatus.isValidInvoiceStatus(paymentStatus);
    }
}
