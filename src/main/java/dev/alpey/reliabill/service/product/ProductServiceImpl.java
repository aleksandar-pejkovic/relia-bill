package dev.alpey.reliabill.service.product;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dev.alpey.reliabill.enums.TaxRate;

public class ProductServiceImpl {

    private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);

    private BigDecimal calculatePriceBeforeTax(BigDecimal price, TaxRate taxRate) {
        BigDecimal recalculatedRate = recalculateRate(taxRate.getRate());
        BigDecimal tax = price.multiply(recalculatedRate)
                .setScale(2, RoundingMode.HALF_EVEN);
        return price.subtract(tax);
    }

    private BigDecimal recalculateRate(BigDecimal rate) {
        return (rate.multiply(HUNDRED_PERCENT))
                .divide(rate.add(HUNDRED_PERCENT), RoundingMode.UNNECESSARY);
    }
}
