package ru.miron.bank.entity.client.validators.login;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class LoginValidator implements ConstraintValidator<ValidLogin, String> {
    public static final int MIN_LENGTH = 5;
    public static final int MAX_LENGTH = 35;
    public static final String REGEX = "^[а-яА-Я\\w\\-\\_\\d]{%d,%d}$".formatted(MIN_LENGTH, MAX_LENGTH);
    public static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return PATTERN.matcher(s).matches();
    }
}
