package ru.miron.bank.entity.client.validators.birthdate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.regex.Pattern;

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
