package dev.alpey.reliabill.enums;

public enum DocumentType {

    INVOICE("Faktura"),
    ESTIMATE("Profaktura");

    private final String type;

    DocumentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean isValidDocumentType(String type) {
        try {
            DocumentType.valueOf(type);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
