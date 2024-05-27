package ru.miron.bank.entity.client.validators.surname;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SurnameValidator implements ConstraintValidator<ValidSurname, String> {
    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 30;
    public static final String REGEX = "[А-Я][а-я]{%d,%d}"
            .formatted(MIN_LENGTH - 1, MAX_LENGTH - 1);
    public static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return PATTERN.matcher(s).matches();
    }
}
