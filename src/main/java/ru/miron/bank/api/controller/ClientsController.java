package ru.miron.bank.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.miron.bank.api.dto.TransferRequestDto;
import ru.miron.bank.api.dto.TransferResponseDto;
import ru.miron.bank.entity.client.validators.phone.ValidPhone;
import ru.miron.bank.exception.AccountSizeException;
import ru.miron.bank.exception.EitherEmailOrPhoneMustBeNotNullException;
import ru.miron.bank.service.ClientService;

import java.util.UUID;

import static ru.miron.bank.util.AuthUtil.getLogin;


@RestController
@RequestMapping("/api/v1/clients")
@AllArgsConstructor
@Slf4j
public class ClientsController {
    private final ClientService service;

    @PutMapping("/self/phone")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPhone(@ValidPhone @RequestBody String phone,
                         Authentication authentication,
                         HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        var client = service.getClient(authentication, log, request, requestUUID);
        log.debug("(%s) (%s) Setting phone to %s for %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                phone,
                getLogin(authentication)
        ));
        client.setPhone(phone);
        service.save(client);
    }

    @DeleteMapping("/self/phone")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhone(Authentication authentication,
                            HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        var client = service.getClient(authentication, log, request, requestUUID);
        if (client.getEmail() == null) {
            log.debug("(%s) (%s) Setting %s's phone failed because either email or phone must be not null".formatted(
                    request.getRequestURI(),
                    requestUUID.toString(),
                    getLogin(authentication)
            ));
            throw new EitherEmailOrPhoneMustBeNotNullException();
        }
        log.debug("(%s) (%s) Setting %s's phone to null".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                getLogin(authentication)
        ));
        client.setPhone(null);
        service.save(client);
    }

    @PutMapping("/self/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setEmail(@Email @RequestBody String email,
                         Authentication authentication,
                         HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        var client = service.getClient(authentication, log, request, requestUUID);
        log.debug("(%s) (%s) Setting email to %s for %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                email,
                getLogin(authentication)
        ));
        client.setEmail(email);
        service.save(client);
    }

    @DeleteMapping("/self/email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmail(Authentication authentication,
                            HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        var client = service.getClient(authentication, log, request, requestUUID);
        if (client.getPhone() == null) {
            log.debug("(%s) (%s) Setting %s's email failed because either email or phone must be not null".formatted(
                    request.getRequestURI(),
                    requestUUID.toString(),
                    getLogin(authentication)
            ));
            throw new EitherEmailOrPhoneMustBeNotNullException();
        }
        log.debug("(%s) (%s) Setting %s's email to null".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                getLogin(authentication)
        ));
        client.setEmail(null);
        service.save(client);
    }

    @PostMapping("/self/transactions/transfer")
    public TransferResponseDto transfer(@RequestBody TransferRequestDto transferRequestDto,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        var senderLogin = getLogin(authentication);
        var receiverLogin = transferRequestDto.getToLogin();
        log.debug("(%s) (%s) sender and receiver logins: %s and %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                senderLogin,
                receiverLogin
        ));
        try {
            var clientsTransfer = service.transfer(senderLogin, receiverLogin, transferRequestDto.getAmount());
            return new TransferResponseDto(TransferResponseDto.State.TRANSFERRED, clientsTransfer.getId());
        } catch (AccountSizeException e) {
            return new TransferResponseDto(TransferResponseDto.State.NOT_ENOUGH_MONEY, null);
        }
    }

}
