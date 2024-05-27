package ru.miron.bank.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessAndRefreshTokenDto {
    private String accessToken;
    private String refreshToken;
}
