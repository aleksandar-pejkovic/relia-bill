package dev.alpey.reliabill.util.calculator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import dev.alpey.reliabill.enums.InvoiceStatus;
import dev.alpey.reliabill.model.Customer;
import dev.alpey.reliabill.model.Invoice;
import dev.alpey.reliabill.model.Payment;

public final class PaymentCalculator {

    private PaymentCalculator() {
    }

    public static void processPayment(Payment payment) {
        Customer customer = payment.getCustomer();

        if (allInvoicesArePaid(customer)) {
            increaseOverpaidAmount(customer, payment.getAmount());
        } else if (payment.getInvoice() != null) {
            paySingleInvoice(payment.getInvoice(), payment.getAmount());
        } else {
            payAllUnpaidInvoices(customer, payment.getAmount());
        }
    }

    private static void paySingleInvoice(Invoice invoice, BigDecimal paymentAmount) {
        BigDecimal remainingDebt = invoice.getRemainingDebt();
        BigDecimal paidAmount = paymentAmount.min(remainingDebt);
        BigDecimal newRemainingDebt = remainingDebt.subtract(paidAmount);
        BigDecimal totalPaidAmount = invoice.getPaidAmount().add(paidAmount);

        invoice.setPaidAmount(totalPaidAmount);
        invoice.setRemainingDebt(newRemainingDebt);

        if (newRemainingDebt.compareTo(BigDecimal.ZERO) == 0) {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
        } else {
            invoice.setInvoiceStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        BigDecimal overpaidAmount = paymentAmount.subtract(remainingDebt);
        if (overpaidAmount.compareTo(BigDecimal.ZERO) > 0) {
            payAllUnpaidInvoices(invoice.getCustomer(), overpaidAmount);
        }
    }

    private static void payAllUnpaidInvoices(Customer customer, BigDecimal paymentAmount) {
        List<Invoice> unpaidInvoices = customer.getInvoices().stream()
                .filter(invoice -> invoice.getInvoiceStatus() != InvoiceStatus.PAID)
                .sorted(Comparator.comparing(Invoice::getCreationDate))
                .toList();

        for (Invoice invoice : unpaidInvoices) {
            BigDecimal remainingDebt = invoice.getRemainingDebt();
            BigDecimal paidAmount = paymentAmount.min(remainingDebt);
            BigDecimal newRemainingDebt = remainingDebt.subtract(paidAmount);
            BigDecimal totalPaidAmount = invoice.getPaidAmount().add(paidAmount);

            invoice.setPaidAmount(totalPaidAmount);
            invoice.setRemainingDebt(newRemainingDebt);

            if (newRemainingDebt.compareTo(BigDecimal.ZERO) == 0) {
                invoice.setInvoiceStatus(InvoiceStatus.PAID);
            } else {
                invoice.setInvoiceStatus(InvoiceStatus.PARTIALLY_PAID);
            }

            paymentAmount = paymentAmount.subtract(paidAmount);
            if (paymentAmount.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        if (paymentAmount.compareTo(BigDecimal.ZERO) > 0) {
            increaseOverpaidAmount(customer, paymentAmount);
        }
    }

    private static boolean allInvoicesArePaid(Customer customer) {
        return customer.getInvoices().stream()
                .allMatch(invoice -> invoice.getRemainingDebt().compareTo(BigDecimal.ZERO) == 0);
    }

    private static void increaseOverpaidAmount(Customer customer, BigDecimal paymentAmount) {
        BigDecimal overpaidAmount = customer.getOverpaidAmount();
        customer.setOverpaidAmount(overpaidAmount.add(paymentAmount));
    }
}
