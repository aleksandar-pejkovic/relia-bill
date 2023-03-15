package dev.alpey.reliabill.configuration.validation.invoice.type;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentTypeValdiator implements ConstraintValidator<DocumentType, String> {

    @Override
    public void initialize(DocumentType documentType) {
    }

    @Override
    public boolean isValid(String documentType, ConstraintValidatorContext context) {
        return dev.alpey.reliabill.enums.DocumentType.isValidDocumentType(documentType);
    }
}
