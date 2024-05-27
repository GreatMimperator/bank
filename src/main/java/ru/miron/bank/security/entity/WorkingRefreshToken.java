package ru.miron.bank.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
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