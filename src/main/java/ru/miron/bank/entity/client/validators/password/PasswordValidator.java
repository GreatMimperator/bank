package ru.miron.bank.entity.client.validators.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    public static final int MIN_LENGTH = 4;
    public static final int MAX_LENGTH = 100;
    public static final String REGEX = "[а-яА-Я\\w\\d_-]{%d,%d}"
            .formatted(MIN_LENGTH, MAX_LENGTH);
    public static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return PATTERN.matcher(s).matches();
    }
}
