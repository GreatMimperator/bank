package ru.miron.bank.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import ru.miron.bank.api.dto.AccessAndRefreshTokenDto;
import ru.miron.bank.api.dto.LoginPasswordDto;
import ru.miron.bank.security.TokenService;
import ru.miron.bank.service.ClientService;

import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private ClientService clientService;
    private TokenService tokenService;
    private PasswordEncoder passwordEncoder;
    private JwtDecoder jwtDecoder;

    @PostMapping("/client/login")
    public ResponseEntity<AccessAndRefreshTokenDto> login(@Valid @RequestBody LoginPasswordDto loginPasswordDto, HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        log.debug("(%s) (%s) Request login is %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                loginPasswordDto.getLogin()
        ));
        var idOpt = loginPasswordDto.getId();
        if (idOpt.isEmpty()) {
            log.debug("(%s) (%s) Request id is not presented".formatted(
                    request.getRequestURI(),
                    requestUUID.toString()
            ));
            throw new IllegalStateException("id not presented");
        }
        var id = idOpt.get();
        log.debug("(%s) (%s) Request id has %s type with %s value".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                id.getFirst(),
                id.getSecond()
        ));
        var clientOpt = switch (id.getFirst()) {
            case LOGIN -> clientService.findByLogin(id.getSecond());
            case EMAIL -> clientService.findByEmail(id.getSecond());
            case PHONE -> clientService.findByPhone(id.getSecond());
        };
        if (clientOpt.isEmpty()) {
            log.debug("(%s) (%s) Has no client with id %s of type %s".formatted(
                    request.getRequestURI(),
                    requestUUID.toString(),
                    id.getSecond(),
                    id.getFirst()
            ));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        var client = clientOpt.get();
        if (!passwordEncoder.matches(loginPasswordDto.getPassword(), client.getEncodedPassword())) {
            log.debug("(%s) (%s) Db client encoded password doesn't match to request password".formatted(
                    request.getRequestURI(),
                    requestUUID.toString()
            ));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        log.debug("(%s) (%s) Db client encoded password matches to request password, generating tokens...".formatted(
                request.getRequestURI(),
                requestUUID.toString()
        ));
        return new ResponseEntity<>(
                new AccessAndRefreshTokenDto(
                        tokenService.generateAccessToken(client.getLogin(), new LinkedList<>()),
                        tokenService.generateRefreshToken(client.getLogin(), Optional.empty())
                ),
                HttpStatus.OK
        );
    }

    @PostMapping("/client/token/refresh")
    public ResponseEntity<AccessAndRefreshTokenDto> refresh(@RequestBody String refreshTokenAsString, HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        log.debug("(%s) (%s) Refresh token is %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                refreshTokenAsString
        ));
        var refreshToken = jwtDecoder.decode(refreshTokenAsString);
        log.debug("(%s) (%s) Login from refresh token is %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                refreshToken.getSubject()
        ));
        if (!tokenService.isWorkingRefreshToken(refreshToken)) {
            log.debug("(%s) (%s) Refresh token with %s jti and %s login is not working".formatted(
                    request.getRequestURI(),
                    requestUUID.toString(),
                    refreshToken.getId(),
                    refreshToken.getSubject()
            ));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        log.debug("(%s) (%s) Refresh token is working, generating new tokens...".formatted(
                request.getRequestURI(),
                requestUUID.toString()
        ));
        var login = refreshToken.getSubject();
        var newAccessToken = tokenService.generateAccessToken(login, new LinkedList<>());
        var newRefreshToken = tokenService.generateRefreshToken(login, Optional.of(refreshToken));
        return new ResponseEntity<>(
                new AccessAndRefreshTokenDto(newAccessToken, newRefreshToken),
                HttpStatus.OK
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/client/logout")
    public void logout(@RequestBody String refreshTokenAsString, HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        log.debug("(%s) (%s) Refresh token is %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                refreshTokenAsString
        ));
        var refreshToken = jwtDecoder.decode(refreshTokenAsString);
        log.debug("(%s) (%s) Refresh token is ok, logging out...".formatted(
                request.getRequestURI(),
                requestUUID.toString()
        ));
        tokenService.logout(refreshToken);
    }
}
