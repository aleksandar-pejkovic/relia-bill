package dev.alpey.reliabill.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class NumberUtils {

    private NumberUtils() {
    }

    public static String formatNumber(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        return decimalFormat.format(number);
    }
}
