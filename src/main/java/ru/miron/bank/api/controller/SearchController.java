package ru.miron.bank.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.miron.bank.api.dto.ClientSearchDto;
import ru.miron.bank.entity.client.validators.birthdate.ValidBirthDate;
import ru.miron.bank.entity.client.validators.middlename.MiddleNameValidator;
import ru.miron.bank.entity.client.validators.name.NameValidator;
import ru.miron.bank.entity.client.validators.phone.ValidPhone;
import ru.miron.bank.entity.client.validators.surname.SurnameValidator;
import ru.miron.bank.service.ClientService;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static ru.miron.bank.util.AuthUtil.getLogin;

@RestController
@RequestMapping("/api/v1/search")
@AllArgsConstructor
@Slf4j
public class SearchController {
    private final ClientService service;

    @GetMapping("/clients")
    public List<ClientSearchDto> filter(@RequestParam(required = false) @ValidBirthDate Timestamp youngerThanOrEqual,
                                        @RequestParam(required = false) @ValidPhone String phone,
                                        @RequestParam(required = false) @Email String email,
                                        @RequestParam(required = false) @Length(max = NameValidator.MAX_LENGTH) String nameBeginsWith,
                                        @RequestParam(required = false) @Length(max = SurnameValidator.MAX_LENGTH) String surnameBeginsWith,
                                        @RequestParam(required = false) @Length(max = MiddleNameValidator.MAX_LENGTH) String middleNameBeginsWith,
                                        @RequestParam(required = false, defaultValue = "1") @Positive int page,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        var requestUUID = UUID.randomUUID();
        log.debug("(%s) (%s) Got query".formatted(request.getRequestURI(), requestUUID.toString()));
        return ClientSearchDto.from(
                service.findWithFilters(
                        youngerThanOrEqual,
                        phone, email,
                        nameBeginsWith, surnameBeginsWith, middleNameBeginsWith,
                        getLogin(authentication),
                        page
                )
        );
    }
}
