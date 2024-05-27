package ru.miron.bank.entity.client.validators.middlename;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MiddleNameValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMiddleName {
    String message() default "Invalid middle name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}