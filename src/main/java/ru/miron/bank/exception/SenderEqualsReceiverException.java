package ru.miron.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Sender shouldn't be equal to receiver")
public class SenderEqualsReceiverException extends IllegalStateException {}
