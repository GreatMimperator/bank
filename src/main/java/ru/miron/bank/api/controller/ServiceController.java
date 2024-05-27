package ru.miron.bank.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.miron.bank.api.dto.ClientCreateDto;
import ru.miron.bank.exception.ClientWithAnyIdAlreadyExistsException;
import ru.miron.bank.service.ClientService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/service")
@AllArgsConstructor
@Slf4j
public class ServiceController {
    private final ClientService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/clients")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addClient(@Valid @RequestBody ClientCreateDto clientCreateDto,
                          HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        log.debug("(%s) (%s) Got ".formatted(
                request.getRequestURI(),
                requestUUID.toString()),
                clientCreateDto
        );
        var client = clientCreateDto.initClient(passwordEncoder);
        if (!service.areIdsUnique(client)) {
            log.debug("(%s) (%s) Some ids are not unique ".formatted(
                    request.getRequestURI(),
                    requestUUID.toString())
            );
            throw new ClientWithAnyIdAlreadyExistsException();
        }
        log.debug("(%s) (%s) Ids are ok, creating...".formatted(
                request.getRequestURI(),
                requestUUID.toString())
        );
        service.save(client);
    }
}
