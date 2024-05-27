package ru.miron.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Client with login in token has not found")
public class ClientNotFoundException extends IllegalStateException {}
