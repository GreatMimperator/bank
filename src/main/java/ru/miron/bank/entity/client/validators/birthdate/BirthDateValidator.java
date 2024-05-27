package ru.miron.bank.entity.client.validators.birthdate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class BirthDateValidator implements ConstraintValidator<ValidBirthDate, Timestamp> {
    public static final int MIN_AGE = 14;

    @Override
    public boolean isValid(Timestamp timestamp, ConstraintValidatorContext constraintValidatorContext) {
        if (timestamp == null) {
            return true;
        }
        return timestamp.before(Timestamp.from(ZonedDateTime.now().minusYears(MIN_AGE).toInstant()));
    }
}
