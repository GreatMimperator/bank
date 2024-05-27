package ru.miron.bank.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Account size is illegal for this operation")
public class AccountSizeException extends IllegalStateException {}
