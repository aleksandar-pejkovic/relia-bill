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
}
