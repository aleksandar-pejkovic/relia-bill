package dev.alpey.reliabill.enums;

public enum DocumentType {

    INVOICE("Invoice"),
    ESTIMATE("Estimate");

    private final String type;

    DocumentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean isValidDocumentType(String type) {
        try {
            InvoiceStatus.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
