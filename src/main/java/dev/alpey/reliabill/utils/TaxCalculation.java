package dev.alpey.reliabill.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.model.dto.finance.InvoiceTaxDetails;
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
        var preTax = calculateItemPreTax(precalculatedTaxRate, price);
        item.setPreTax(preTax);
        var total = calculateItemTotal(price, quantity);
        item.setTotal(total);
        var tax = calculateTax(precalculatedTaxRate, total);
        item.setTax(tax);
        var subtotal = total - tax;
        item.setSubtotal(subtotal);
    }

    public static InvoiceTaxDetails getInvoiceTaxDetails(Invoice invoice) {
        var tax = invoice.getItems().stream()
                .mapToDouble(Item::getTax)
                .sum();
        var subtotal = invoice.getTotal() - tax;
        var totalFor0 = calculateTaxAmountForTaxRate(TaxRate.RATE_0, invoice);
        var totalFor10 = calculateTaxAmountForTaxRate(TaxRate.RATE_10, invoice);
        var totalFor20 = invoice.getTotal() - totalFor10 - totalFor0;

        var taxFor10 = calculateTax(totalFor10, LOWER_PRECALCULATED_TAX_RATE);
        var subtotalFor10 = totalFor10 - taxFor10;

        var taxFor20 = calculateTax(totalFor20, HIGHER_PRECALCULATED_TAX_RATE);
        var subtotalFor20 = totalFor20 - taxFor20;

        return InvoiceTaxDetails.builder()
                .totalFor20(totalFor20)
                .totalFor10(totalFor10)
                .totalFor0(totalFor0)
                .tax(tax)
                .taxFor20(taxFor20)
                .taxFor10(taxFor10)
                .subtotal(subtotal)
                .subtotalFor20(subtotalFor20)
                .subtotalFor10(subtotalFor10)
                .build();
    }

    private static double calculateTax(double precalculatedTaxRate, double total) {
        return BigDecimal.valueOf(total).multiply(BigDecimal.valueOf(precalculatedTaxRate))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private static double calculateItemTotal(Double price, Double quantity) {
        return BigDecimal.valueOf(quantity * price)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private static double calculateItemPreTax(double precalculatedTaxRate, Double price) {
        return BigDecimal.valueOf(price - (price * precalculatedTaxRate))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
    }

    private static double calculateTaxAmountForTaxRate(TaxRate taxRate, Invoice invoice) {
        return invoice.getItems().stream()
                .filter(item -> item.getTaxRate() == taxRate)
                .mapToDouble(Item::getTotal)
                .sum();
    }

    private static double calculatePrecalculatedTaxRate(int taxRate) {
        if (taxRate == HIGHER_TAX_RATE) {
            return HIGHER_PRECALCULATED_TAX_RATE;
        } else if (taxRate == LOWER_TAX_RATE) {
            return LOWER_PRECALCULATED_TAX_RATE;
        } else {
            return 0.0;
        }
    }
}
