package ru.miron.bank.util;

public class ValidationUtil {
    public static <T>T getFirstNotNull(T... values) {
        for (T value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
