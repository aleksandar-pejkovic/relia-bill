package dev.alpey.reliabill.util.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

import dev.alpey.reliabill.model.Item;

public final class ItemCalculator {

    private ItemCalculator() {
    }

    public static void calculateAll(Item item) {
        item.setTotal(calculateItemTotal(item));
        item.setTax(calculateItemTax(item));
        item.setSubtotal(calculateItemSubtotal(item));
    }

    private static BigDecimal calculateItemTotal(Item item) {
        return item.getQuantity().multiply(item.getProduct().getPrice())
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal calculateItemTax(Item item) {
        return item.getTotal().multiply(item.getProduct().getTaxRate().getRate())
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal calculateItemSubtotal(Item item) {
        return item.getTotal().subtract(item.getTax());
    }
}
