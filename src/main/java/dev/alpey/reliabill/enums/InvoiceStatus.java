package dev.alpey.reliabill.enums;

public enum InvoiceStatus {

    PENDING("Neizmireno"),
    PARTIALLY_PAID("Delimično izmireno"),
    PAID("Plaćeno");

    private final String status;

    InvoiceStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return status;
    }

    public static boolean isValidInvoiceStatus(String status) {
        for (InvoiceStatus invoiceStatus : InvoiceStatus.values()) {
            if (invoiceStatus.getType().equals(status)) {
                return true;
            }
        }
        return false;
    }
}
