package ru.miron.bank.api.dto.validator.clientdto;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.miron.bank.api.dto.ClientCreateDto;

public class ClientDtoValidator implements ConstraintValidator<ValidClientDto, ClientCreateDto> {

    @Override
    public boolean isValid(ClientCreateDto clientCreateDto, ConstraintValidatorContext constraintValidatorContext) {
        return clientCreateDto.getEmail() != null && clientCreateDto.getPhone() != null;
    }
}
