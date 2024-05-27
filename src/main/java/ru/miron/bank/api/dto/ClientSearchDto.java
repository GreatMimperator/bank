package ru.miron.bank.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.miron.bank.entity.client.Client;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ClientSearchDto {
    public String login;

    public static List<ClientSearchDto> from(List<Client> clients) {
        return clients.stream()
                .map(c -> new ClientSearchDto(c.getLogin()))
                .toList();
    }
}
