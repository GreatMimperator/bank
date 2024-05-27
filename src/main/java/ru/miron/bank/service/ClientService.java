package ru.miron.bank.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.miron.bank.entity.client.Client;
import ru.miron.bank.entity.client.ClientRepository;
import ru.miron.bank.entity.clientstransfer.ClientTransferRepository;
import ru.miron.bank.entity.clientstransfer.ClientsTransfer;
import ru.miron.bank.exception.AccountSizeException;
import ru.miron.bank.exception.ClientNotFoundException;
import ru.miron.bank.exception.SenderEqualsReceiverException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.miron.bank.util.AuthUtil.getLogin;

@Service
@AllArgsConstructor
@Slf4j
public class ClientService {
    private ClientRepository clientRepository;
    private ClientTransferRepository clientTransferRepository;
    private PasswordEncoder passwordEncoder;

    private EntityManager em;

    public boolean hasAuth(Pair<Client.IdType, String> id, String password) {
        var encodedPassword = passwordEncoder.encode(password);
        return switch (id.getFirst()) {
            case LOGIN -> clientRepository.existsByLoginAndEncodedPassword(id.getSecond(), encodedPassword);
            case EMAIL -> clientRepository.existsByEmailAndEncodedPassword(id.getSecond(), encodedPassword);
            case PHONE -> clientRepository.existsByPhoneAndEncodedPassword(id.getSecond(), encodedPassword);
        };
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public Optional<Client> findByLogin(String login) {
        return clientRepository.findById(login);
    }

    public Optional<Client> findByPhone(String login) {
        return clientRepository.findByPhone(login);
    }

    public Client save(Client client) {
        return clientRepository.save(client);
    }

    public Client getClient(Authentication authentication, Logger log, HttpServletRequest request, UUID requestUUID) {
        var login = getLogin(authentication);
        log.debug("(%s) (%s) Login is %s".formatted(
                request.getRequestURI(),
                requestUUID.toString(),
                login
        ));
        var clientOpt = findByLogin(login);
        if (clientOpt.isEmpty()) {
            log.debug("(%s) (%s) Client with login %s has not found".formatted(
                    request.getRequestURI(),
                    requestUUID.toString(),
                    login
            ));
            throw new ClientNotFoundException();
        }
        return clientOpt.get();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ClientsTransfer transfer(String senderLogin, String receiverLogin, BigDecimal amount) {
        if (senderLogin.equals(receiverLogin)) {
            throw new SenderEqualsReceiverException();
        }
        var senderOpt = clientRepository.findById(senderLogin);
        var receiverOpt = clientRepository.findById(receiverLogin);
        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            throw new ClientNotFoundException();
        }
        var sender = senderOpt.get();
        var receiver = receiverOpt.get();
        if (sender.getAccountSize().compareTo(amount) < 0) {
            throw new AccountSizeException();
        }
        sender.setAccountSize(sender.getAccountSize().subtract(amount));
        receiver.setAccountSize(receiver.getAccountSize().add(amount));
        sender = clientRepository.save(sender);
        receiver = clientRepository.save(receiver);
        return clientTransferRepository.save(
                new ClientsTransfer(
                        null,
                        sender,
                        receiver,
                        amount,
                        Timestamp.from(Instant.now())
                )
        );
    }

    public boolean areIdsUnique(Client client) {
        var noSuchLogin = clientRepository.findById(client.getLogin()).isEmpty();
        var noSuchEmail = true;
        if (client.getEmail() != null) {
            noSuchEmail = clientRepository.findByEmail(client.getEmail()).isEmpty();
        }
        var noSuchPhone = true;
        if (client.getPhone() != null) {
            noSuchPhone = clientRepository.findByPhone(client.getPhone()).isEmpty();
        }
        return noSuchLogin && noSuchEmail && noSuchPhone;
    }

    public List<Client> findWithFilters(Timestamp youngerThanOrEqual,
                                        String phone, String email,
                                        String nameBeginsWith, String surnameBeginsWith, String middleNameBeginsWith,
                                        String excludeClientLogin,
                                        int page) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Client.class);
        var clientRoot = cq.from(Client.class);
        cq.select(clientRoot);
        var filters = new LinkedList<>();
        if (youngerThanOrEqual != null) {
            filters.add(cb.greaterThanOrEqualTo(clientRoot.get("birthDate"), youngerThanOrEqual));
        }
        if (phone != null) {
            filters.add(cb.equal(clientRoot.get("phone"), phone));
        }
        if (email != null) {
            filters.add(cb.equal(clientRoot.get("email"), email));
        }
        if (nameBeginsWith != null) {
            filters.add(cb.like(clientRoot.get("name"), nameBeginsWith + "%"));
        }
        if (surnameBeginsWith != null) {
            filters.add(cb.like(clientRoot.get("surname"), surnameBeginsWith + "%"));
        }
        if (middleNameBeginsWith != null) {
            filters.add(cb.like(clientRoot.get("middle_name"), middleNameBeginsWith + "%"));
        }
        filters.add(cb.notEqual(clientRoot.get("login"), excludeClientLogin));
        cq.where(cb.and(filters.toArray(new Predicate[0])));
        var query = em.createQuery(cq);
        applyPagination(query, Pageable.ofSize(20).withPage(page - 1));
        return query.getResultList();
    }

    private void applyPagination(TypedQuery<?> query, Pageable pageable) {
        query.setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize());
    }

    public List<Client> getAll() {
        return clientRepository.findAll();
    }
}
