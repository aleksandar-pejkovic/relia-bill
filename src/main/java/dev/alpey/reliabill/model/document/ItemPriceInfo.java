package dev.alpey.reliabill.model.document;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dev.alpey.reliabill.enums.TaxRate;
import dev.alpey.reliabill.model.product.Product;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemPriceInfo {

    private TaxRate taxRate;

    private BigDecimal total;

    private BigDecimal tax;

    private BigDecimal subtotal;

    public ItemPriceInfo(BigDecimal quantity, Product product, TaxRate taxRate) {
        this.total = quantity.multiply(product.getPrice())
                .setScale(2, RoundingMode.HALF_EVEN);
        this.taxRate = taxRate;
        setTax();
        setSubtotal();
    }

    public void setTax() {
        this.tax = total.multiply(taxRate.getRate())
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    public void setSubtotal() {
        this.subtotal = this.total.subtract(this.tax);
    }
}
