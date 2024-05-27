package ru.miron.bank.entity.client;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "login")
public class Client {
    @Id
    private String login;

    private String email;
    private String phone;

    private Timestamp birthDate;

    private String name;
    private String surname;
    private String middleName;

    private String encodedPassword;

    private BigDecimal accountStartSize;
    private BigDecimal accountSize;

    public enum IdType {
        LOGIN, EMAIL, PHONE
    }
}
