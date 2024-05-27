package ru.miron.bank.background.sheduled;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.miron.bank.entity.client.Client;
import ru.miron.bank.entity.client.ClientRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Slf4j
public class ClientActionsScheduler {

    public final ClientRepository repository;
    public final Increaser increaser;


    @Scheduled(fixedRate = 60000)
    public void increaseAccountSize() {
        log.debug("Count: %d".formatted(repository.findAll().size()));
        var clientLogins = repository.findAll()
                .stream().map(Client::getLogin)
                .toList();
        log.debug("Clients to check for account increase: %d".formatted(clientLogins.size()));
        for (var clientLogin : clientLogins) {
            increaser.increaseAccountSize(clientLogin);
        }
    }

    @Service
    @AllArgsConstructor
    @Slf4j
    public static class Increaser {
        public final ClientRepository repository;

        public static final Double INCREASE_PERCENTAGE = 5.0;
        public static final BigDecimal INCREASE_MULTIPLIER = BigDecimal.valueOf(1 + INCREASE_PERCENTAGE / 100);
        public static final Double MAX_ACCOUNT_INCREASE_PERCENTAGE = 207.0;
        public static final BigDecimal MAX_ACCOUNT_INCREASE_MULTIPLIER = BigDecimal.valueOf(MAX_ACCOUNT_INCREASE_PERCENTAGE / 100);

        @Transactional(isolation = Isolation.SERIALIZABLE)
        public void increaseAccountSize(String clientLogin) {
            var clientOpt = repository.findById(clientLogin);
            if (clientOpt.isEmpty()) {
                log.debug("Somehow Client with login %s does not exist".formatted(clientLogin));
                return;
            }
            var client = clientOpt.get();
            log.debug("Client with login %s has %f money on account".formatted(clientLogin, client.getAccountSize()));
            var maxAccountSizeIncreaseTo = client.getAccountStartSize().multiply(MAX_ACCOUNT_INCREASE_MULTIPLIER);
            log.debug("Client with login %s has %f money as ceil because base account money are %f".formatted(
                    clientLogin,
                    maxAccountSizeIncreaseTo,
                    client.getAccountStartSize()
            ));
            if (client.getAccountSize().compareTo(maxAccountSizeIncreaseTo) >= 0) {
                log.debug("Client with login %s already has too much money".formatted(clientLogin));
                return;
            }
            var increasedAccountSize = client.getAccountSize().multiply(INCREASE_MULTIPLIER);
            if (increasedAccountSize.compareTo(maxAccountSizeIncreaseTo) >= 0) {
                client.setAccountSize(maxAccountSizeIncreaseTo);
            } else {
                client.setAccountSize(increasedAccountSize);
            }
            log.debug("Client with login %s increased account size to %f".formatted(clientLogin, client.getAccountSize()));
            repository.save(client);
        }
    }
}
