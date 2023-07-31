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
        for (DocumentType documentType : DocumentType.values()) {
            if (documentType.getType().equals(type)) {
                return true;
            }
        }
        return false;
    }
}
