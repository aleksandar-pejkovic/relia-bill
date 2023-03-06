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
public class DocumentPriceInfo {

    private BigDecimal total;

    private BigDecimal tax;

    private BigDecimal subtotal;

    private BigDecimal taxRate20total;

    private BigDecimal taxRate20tax;

    private BigDecimal taxRate20subtotal;

    private BigDecimal taxRate10total;

    private BigDecimal taxRate10tax;

    private BigDecimal taxRate10subtotal;

    public DocumentPriceInfo(Set<Item> items) {
        this.total = items.stream()
                .map(item -> item.getItemPriceInfo().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.tax = items.stream()
                .map(item -> item.getItemPriceInfo().getTax())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.subtotal = this.total.subtract(this.tax);

        calculatePriceInfoPerTaxRate(items);
    }

    private void calculatePriceInfoPerTaxRate(Set<Item> items) {
        calculatePriceInfoForRate10(items);
        calculatePriceInfoForRate20();
    }

    private void calculatePriceInfoForRate10(Set<Item> allItems) {
        Set<Item> items = allItems.stream()
                .filter(item -> item.getItemPriceInfo().getTaxRate().equals(TaxRate.RATE_10))
                .collect(Collectors.toSet());

        this.taxRate10total = items.stream()
                .map(item -> item.getItemPriceInfo().getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.taxRate10tax = items.stream()
                .map(item -> item.getItemPriceInfo().getTax())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.taxRate10subtotal = this.taxRate10total.subtract(this.taxRate10tax);
    }

    private void calculatePriceInfoForRate20() {
        this.taxRate20total = this.total.subtract(this.taxRate10total);
        this.taxRate20tax = this.tax.subtract(this.taxRate10tax);
        this.taxRate20subtotal = this.subtotal.subtract(this.taxRate10subtotal);
    }
}
