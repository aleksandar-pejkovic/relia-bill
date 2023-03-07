package dev.alpey.reliabill.model.product;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dev.alpey.reliabill.enums.TaxRate;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetails {

    private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);

    private BigDecimal priceBeforeTax;

    @Enumerated(EnumType.STRING)
    private TaxRate taxRate;

    private BigDecimal price;

    public ProductDetails(BigDecimal price, TaxRate taxRate) {
        BigDecimal recalculatedRate = recalculateRate(taxRate.getRate());
        this.price = price;
        this.taxRate = taxRate;
        BigDecimal tax = price.multiply(recalculatedRate)
                .setScale(2, RoundingMode.HALF_EVEN);
        this.priceBeforeTax = this.price.subtract(tax);
    }

    private BigDecimal recalculateRate(BigDecimal rate) {
        return (rate.multiply(HUNDRED_PERCENT))
                .divide(rate.add(HUNDRED_PERCENT), RoundingMode.UNNECESSARY);
    }
}
