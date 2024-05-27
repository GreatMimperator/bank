package ru.miron.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Either email or phone must be not null")
public class EitherEmailOrPhoneMustBeNotNullException extends IllegalStateException {}
