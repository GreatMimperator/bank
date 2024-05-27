package ru.miron.bank.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessAndRefreshTokenDto {
    private String accessToken;
    private String refreshToken;
}
