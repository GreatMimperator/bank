package ru.miron.bank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Client with login, email or phone already exists")
public class ClientWithAnyIdAlreadyExistsException extends IllegalStateException {}
