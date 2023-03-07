package dev.alpey.reliabill.model.document;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import dev.alpey.reliabill.enums.TaxRate;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentPriceDetails {

    private BigDecimal total;

    private BigDecimal tax;

    private BigDecimal subtotal;

    private BigDecimal taxRate20total;

    private BigDecimal taxRate20tax;

    private BigDecimal taxRate20subtotal;

    private BigDecimal taxRate10total;

    private BigDecimal taxRate10tax;

    private BigDecimal taxRate10subtotal;

    public DocumentPriceDetails(Set<Item> items) {
        this.total = calculateTotal(items);

        this.tax = calculateTax(items);

        this.subtotal = this.total.subtract(this.tax);

        calculatePriceInfoPerTaxRate(items);
    }

    private void calculatePriceInfoPerTaxRate(Set<Item> items) {
        calculatePriceInfoForRate10(items);
        calculatePriceInfoForRate20();
    }

    private void calculatePriceInfoForRate10(Set<Item> allItems) {
        Set<Item> items = allItems.stream()
                .filter(item -> item.getProduct().getProductDetails().getTaxRate().equals(TaxRate.RATE_10))
                .collect(Collectors.toSet());

        this.taxRate10total = calculateTotal(items);

        this.taxRate10tax = calculateTax(items);

        this.taxRate10subtotal = this.taxRate10total.subtract(this.taxRate10tax);
    }

    private void calculatePriceInfoForRate20() {
        this.taxRate20total = this.total.subtract(this.taxRate10total);
        this.taxRate20tax = this.tax.subtract(this.taxRate10tax);
        this.taxRate20subtotal = this.subtotal.subtract(this.taxRate10subtotal);
    }

    private static BigDecimal calculateTotal(Set<Item> items) {
        return items.stream()
                .map(item -> item.getItemPriceDetails().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTax(Set<Item> items) {
        return items.stream()
                .map(item -> item.getItemPriceDetails().getTax())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
