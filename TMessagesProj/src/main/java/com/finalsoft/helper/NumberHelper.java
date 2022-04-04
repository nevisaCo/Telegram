package com.finalsoft.helper;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberHelper {
    public static String format(int number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}
