package dev.alpey.reliabill.util.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dev.alpey.reliabill.model.Product;

public final class ProductCalculator {

    private static final BigDecimal HUNDRED_PERCENT = BigDecimal.valueOf(100);

    private ProductCalculator() {
    }

    public static void calculateAll(Product product) {
        product.setPriceBeforeTax(calculatePriceBeforeTax(product));
    }

    private static BigDecimal calculatePriceBeforeTax(Product product) {
        BigDecimal recalculatedRate = recalculateRate(product.getTaxRate().getRate());
        BigDecimal tax = product.getPrice().multiply(recalculatedRate)
                .setScale(2, RoundingMode.HALF_EVEN);
        return product.getPrice().subtract(tax);
    }

    private static BigDecimal recalculateRate(BigDecimal rate) {
        return (rate.multiply(HUNDRED_PERCENT))
                .divide(rate.add(HUNDRED_PERCENT), RoundingMode.UNNECESSARY);
    }
}
