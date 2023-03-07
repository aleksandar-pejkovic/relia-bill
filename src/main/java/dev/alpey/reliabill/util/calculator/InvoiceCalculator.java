package dev.alpey.reliabill.util.calculator;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.model.Invoice;
import dev.alpey.reliabill.model.Item;

public final class InvoiceCalculator {

    private InvoiceCalculator() {
    }

    public static void calculateAll(Invoice invoice) {
        invoice.setTotal(calculateTotal(invoice.getItems()));
        invoice.setTax(calculateTax(invoice.getItems()));
        invoice.setSubtotal(invoice.getTotal().subtract(invoice.getTax()));
        calculateTaxDetailsPerTaxRate(invoice);
    }

    private static void calculateTaxDetailsPerTaxRate(Invoice invoice) {
        calculateTaxDetailsForRate10(invoice);
        calculateTaxDetailsForRate20(invoice);
    }

    private static void calculateTaxDetailsForRate10(Invoice invoice) {
        Set<Item> items = invoice.getItems().stream()
                .filter(item -> item.getProduct().getTaxRate().equals(TaxRate.RATE_10))
                .collect(Collectors.toSet());

        invoice.setTaxRate10total(calculateTotal(items));
        invoice.setTaxRate10tax(calculateTax(items));
        invoice.setSubtotal(invoice.getTaxRate10total().subtract(invoice.getTaxRate10tax()));
    }

    private static void calculateTaxDetailsForRate20(Invoice invoice) {
        invoice.setTaxRate20total(invoice.getTotal().subtract(invoice.getTaxRate10total()));
        invoice.setTaxRate20tax(invoice.getTax().subtract(invoice.getTaxRate10tax()));
        invoice.setTaxRate20subtotal(invoice.getSubtotal().subtract(invoice.getTaxRate20subtotal()));
    }

    private static BigDecimal calculateTotal(Set<Item> items) {
        return items.stream()
                .map(Item::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTax(Set<Item> items) {
        return items.stream()
                .map(Item::getTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
