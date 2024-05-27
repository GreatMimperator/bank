package ru.miron.bank.entity.clientstransfer;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.miron.bank.entity.client.Client;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientsTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sender_login")
    private Client sender;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receiver_login")
    private Client receiver;

    private BigDecimal amount;

    private Timestamp timestamp;
}