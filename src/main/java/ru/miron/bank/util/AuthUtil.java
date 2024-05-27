package ru.miron.bank.util;

import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import ru.miron.bank.entity.client.Client;

public class AuthUtil {
    public static String getLogin(Authentication authentication) {
        return authentication.getName();
    }
}
