package dev.alpey.reliabill.enums;

public enum InvoiceStatus {

    PENDING,
    PARTIALLY_PAID,
    PAID;

    public static boolean isValidInvoiceStatus(String status) {
        try {
            InvoiceStatus.valueOf(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
