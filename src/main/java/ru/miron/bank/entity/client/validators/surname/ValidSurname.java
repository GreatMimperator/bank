package ru.miron.bank.entity.client.validators.surname;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SurnameValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSurname {
    String message() default "Invalid surname";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}