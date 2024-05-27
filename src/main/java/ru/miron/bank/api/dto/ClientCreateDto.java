package ru.miron.bank.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.miron.bank.entity.client.Client;
import ru.miron.bank.entity.client.validators.birthdate.ValidBirthDate;
import ru.miron.bank.entity.client.validators.login.ValidLogin;
import ru.miron.bank.entity.client.validators.middlename.ValidMiddleName;
import ru.miron.bank.entity.client.validators.name.ValidName;
import ru.miron.bank.entity.client.validators.password.ValidPassword;
import ru.miron.bank.entity.client.validators.phone.ValidPhone;
import ru.miron.bank.entity.client.validators.surname.ValidSurname;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ClientCreateDto {
    @ValidLogin
    @NotNull
    private String login;
    @Email
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;
    @ValidPhone
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phone;
    @ValidBirthDate
    private Timestamp birthDate;
    @ValidName
    @NotNull
    private String name;
    @ValidSurname
    @NotNull
    private String surname;
    @ValidMiddleName
    @NotNull
    private String middleName;
    @ValidPassword
    @NotNull
    private String password;
    @Min(0)
    @NotNull
    private BigDecimal accountSize;

    public Client initClient(PasswordEncoder passwordEncoder) {
        return new Client(
                login,
                email, phone,
                birthDate,
                name, surname, middleName,
                passwordEncoder.encode(password),
                accountSize,
                accountSize
        );
    }
}
