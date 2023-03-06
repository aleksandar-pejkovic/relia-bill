package dev.alpey.reliabill.enums;

import java.math.BigDecimal;

public enum TaxRate {

    RATE_10(BigDecimal.valueOf(0.1)),
    RATE_20(BigDecimal.valueOf(0.2));

    private final BigDecimal rate;

    TaxRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
