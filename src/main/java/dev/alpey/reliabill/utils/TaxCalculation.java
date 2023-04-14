package dev.alpey.reliabill.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dev.alpey.reliabill.model.entity.Invoice;
import dev.alpey.reliabill.model.entity.Item;

public final class TaxCalculation {

    private static final int HIGHER_TAX_RATE = 20;

    private static final int LOWER_TAX_RATE = 10;

    private static final double HIGHER_PRECALCULATED_TAX_RATE = 0.1666666666666667;

    private static final double LOWER_PRECALCULATED_TAX_RATE = 0.09090909090909091;

    private TaxCalculation() {
    }

    public static void calculateTax(Item item) {
        double precalculatedTaxRate = calculatePrecalculatedTaxRate(item.getTaxRate().getRate());
        var price = item.getPrice();
        var quantity = item.getQuantity();
        var preTax = BigDecimal.valueOf(price - (price * precalculatedTaxRate))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        item.setPreTax(preTax);
        var total = BigDecimal.valueOf(quantity * price)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        item.setTotal(total);
        var tax = BigDecimal.valueOf(total).multiply(BigDecimal.valueOf(precalculatedTaxRate))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        item.setTax(tax);
        var subtotal = total - tax;
        item.setSubtotal(subtotal);
    }

    public static void calculateTax(Invoice invoice) {
        var total = invoice.getItems().stream()
                .mapToDouble(Item::getTotal)
                .sum();
        invoice.setTotal(total);
        var tax = invoice.getItems().stream()
                .mapToDouble(Item::getTax)
                .sum();
        invoice.setTax(tax);
        var subtotal = total - tax;
        invoice.setSubtotal(subtotal);
    }

    private static double calculatePrecalculatedTaxRate(int taxRate) {
        if (taxRate == HIGHER_TAX_RATE) {
            return HIGHER_PRECALCULATED_TAX_RATE;
        } else if (taxRate == LOWER_TAX_RATE) {
            return LOWER_PRECALCULATED_TAX_RATE;
        } else {
            return 1.0;
        }
    }
}
