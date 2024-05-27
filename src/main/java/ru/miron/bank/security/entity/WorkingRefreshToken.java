package ru.miron.bank.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkingRefreshToken {
    @Id
    @Column(unique = true, nullable = false)
    private String jti;
    private String login;
}