package ru.miron.bank.background.sheduled;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.Retryable;
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
@Profile("!test")
public class ClientActionsScheduler {

    public final ClientRepository repository;
    public final AccountIncreaser accountIncreaser;


    @Scheduled(fixedRateString = "${scheduler.client.account-size.increase-delay-ms}")
    public void increaseAccountSize() {
        log.debug("Count: %d".formatted(repository.findAll().size()));
        var clientLogins = repository.findAll()
                .stream().map(Client::getLogin)
                .toList();
        log.debug("Clients to check for account increase: %d".formatted(clientLogins.size()));
        for (var clientLogin : clientLogins) {
            accountIncreaser.increaseAccountSize(clientLogin);
        }
    }

    @Service
    @Slf4j
    public static class AccountIncreaser {
        public final ClientRepository repository;

        public final BigDecimal increaseMultiplier;
        public final BigDecimal increaseMaxFromBaseMultiplier;

        public AccountIncreaser(ClientRepository repository,
                                @Value("${scheduler.client.account-size.increase-percentage}") Double increasePercentage,
                                @Value("${scheduler.client.account-size.increase-max-from-base-percentage}") Double increaseMaxFromBasePercentage) {
            this.repository = repository;
            this.increaseMultiplier = BigDecimal.valueOf(1 + increasePercentage / 100);
            this.increaseMaxFromBaseMultiplier = BigDecimal.valueOf(increaseMaxFromBasePercentage / 100);
        }


        @Transactional(isolation = Isolation.SERIALIZABLE)
        @Retryable
        public void increaseAccountSize(String clientLogin) {
            var clientOpt = repository.findById(clientLogin);
            if (clientOpt.isEmpty()) {
                log.debug("Somehow Client with login %s does not exist".formatted(clientLogin));
                return;
            }
            var client = clientOpt.get();
            log.debug("Client with login %s has %f money on account".formatted(clientLogin, client.getAccountSize()));
            var maxAccountSizeIncreaseTo = client.getAccountStartSize().multiply(increaseMaxFromBaseMultiplier);
            log.debug("Client with login %s has %f money as ceil because base account money are %f".formatted(
                    clientLogin,
                    maxAccountSizeIncreaseTo,
                    client.getAccountStartSize()
            ));
            if (client.getAccountSize().compareTo(maxAccountSizeIncreaseTo) >= 0) {
                log.debug("Client with login %s already has too much money".formatted(clientLogin));
                return;
            }
            var increasedAccountSize = client.getAccountSize().multiply(increaseMultiplier);
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
