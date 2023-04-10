package dev.alpey.reliabill.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;

import dev.alpey.reliabill.model.dto.ItemDto;
import dev.alpey.reliabill.service.ItemService;

public final class TaxCalculation {

    @Autowired
    private ItemService itemService;

    private static final int HIGHER_TAX_RATE = 20;

    private static final int LOWER_TAX_RATE = 10;

    private static final double HIGHER_PRECALCULATED_TAX_RATE = 0.1666666666666667;

    private static final double LOWER_PRECALCULATED_TAX_RATE = 0.1666666666666667;

    private TaxCalculation() {
    }

    public static void calculateTax(ItemDto itemDto) {
        double precalculatedTaxRate;
        if (itemDto.getTaxRate() == HIGHER_TAX_RATE) {
            precalculatedTaxRate = HIGHER_PRECALCULATED_TAX_RATE;
        } else if (itemDto.getTaxRate() == LOWER_TAX_RATE) {
            precalculatedTaxRate = LOWER_PRECALCULATED_TAX_RATE;
        } else {
            precalculatedTaxRate = 1.0;
        }
        var price = itemDto.getPrice();
        var quantity = itemDto.getQuantity();
        var preTax = BigDecimal.valueOf(price - (price * precalculatedTaxRate))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        itemDto.setPreTax(preTax);
        var total = BigDecimal.valueOf(quantity * price)
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        itemDto.setTotal(total);
        var tax = BigDecimal.valueOf(total).multiply(BigDecimal.valueOf(precalculatedTaxRate))
                .setScale(2, RoundingMode.HALF_EVEN)
                .doubleValue();
        itemDto.setTax(tax);
        var subtotal = total - tax;
        itemDto.setSubtotal(subtotal);
    }
}
