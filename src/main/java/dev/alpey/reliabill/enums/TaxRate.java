package dev.alpey.reliabill.enums;

public enum TaxRate {

    RATE_0(0),
    RATE_10(10),
    RATE_20(20);

    private final Integer rate;

    public static TaxRate fromRate(int rate) {
        for (TaxRate taxRate : TaxRate.values()) {
            if (taxRate.getRate() == rate) {
                return taxRate;
            }
        }
        return null;
    }

    TaxRate(Integer rate) {
        this.rate = rate;
    }

    public Integer getRate() {
        return rate;
    }
}
