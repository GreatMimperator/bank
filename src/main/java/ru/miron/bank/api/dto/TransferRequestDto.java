package ru.miron.bank.api.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.miron.bank.entity.client.validators.login.ValidLogin;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto {
    @ValidLogin
    private String toLogin;
    @Min(0)
    private BigDecimal amount;
}
