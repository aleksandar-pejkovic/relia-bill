package dev.alpey.reliabill.enums;

public enum InvoiceStatus {

    PENDING("Pending"),
    PARTIALLY_PAID("Partially paid"),
    PAID("Paid");

    private final String status;

    InvoiceStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return status;
    }

    public static boolean isValidInvoiceStatus(String status) {
        try {
            InvoiceStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
