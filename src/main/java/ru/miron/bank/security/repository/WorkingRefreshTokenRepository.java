package ru.miron.bank.security.repository;

import org.springframework.data.repository.CrudRepository;
import ru.miron.bank.security.entity.WorkingRefreshToken;

public interface WorkingRefreshTokenRepository extends CrudRepository<WorkingRefreshToken, String> {
    void deleteByLogin(String login);
}