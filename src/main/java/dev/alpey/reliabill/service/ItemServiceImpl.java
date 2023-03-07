package dev.alpey.reliabill.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ItemServiceImpl {

    private BigDecimal calculateTotal(BigDecimal quantity, BigDecimal price) {
        return quantity.multiply(price)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateTax(BigDecimal total, BigDecimal taxRate) {
        return total.multiply(taxRate)
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    private BigDecimal calculateSubtotal(BigDecimal total, BigDecimal tax) {
        return total.subtract(tax);
    }
}
