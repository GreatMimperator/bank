package ru.miron.bank.entity.client;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends CrudRepository<Client, String> {
    boolean existsByLoginAndEncodedPassword(String login, String encodedPassword);
    boolean existsByEmailAndEncodedPassword(String email, String encodedPassword);
    boolean existsByPhoneAndEncodedPassword(String phone, String encodedPassword);

    Optional<Client> findByEmail(String email);
    Optional<Client> findByPhone(String phone);
}
