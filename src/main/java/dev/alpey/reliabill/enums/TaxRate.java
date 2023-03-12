package dev.alpey.reliabill.enums;

public enum TaxRate {

    RATE_10(0.1),
    RATE_20(0.2);

    private final Double rate;

    TaxRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate() {
        return rate;
    }
}
