package ru.miron.bank.entity.client.validators.phone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    /**
     * <pre>
     * international code (1 to 3 nums) + operator (city) code (3 nums) + individual (3 + 2 + 2 nums)
     *
     * 1 group is international code
     * 2 group is operator (city) code
     * 3 group is individual
     * </pre>
     */
    public static final String REGEX = "^(\\d{1,3})(\\d{3})(\\d{%d})$".formatted(3 + 2 + 2);

    public static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return PATTERN.matcher(s).matches();
    }
}
