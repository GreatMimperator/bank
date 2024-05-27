package ru.miron.bank.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;
import ru.miron.bank.entity.client.Client;
import ru.miron.bank.entity.client.validators.login.ValidLogin;
import ru.miron.bank.entity.client.validators.password.ValidPassword;
import ru.miron.bank.entity.client.validators.phone.ValidPhone;

import java.util.Optional;

@Data
@NoArgsConstructor
public class LoginPasswordDto {
    @ValidLogin
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String login;
    @Email
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @ValidPhone
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;
    @ValidPassword
    private String password;

    public LoginPasswordDto(String login, String password) {
        setLogin(login);
        setPassword(password);
    }

    public Optional<Pair<Client.IdType, String>> getId() {
        if (login != null) {
            return Optional.of(Pair.of(Client.IdType.LOGIN, login));
        }
        if (email != null) {
            return Optional.of(Pair.of(Client.IdType.EMAIL, email));
        }
        if (phone != null) {
            return Optional.of(Pair.of(Client.IdType.PHONE, phone));
        }
        return Optional.empty();
    }
}
