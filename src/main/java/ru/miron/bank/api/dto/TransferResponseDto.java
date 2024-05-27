package ru.miron.bank.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransferResponseDto {
    private State state;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long transferId;

    public enum State {
        TRANSFERRED,
        NOT_ENOUGH_MONEY
        // future
    }
}
