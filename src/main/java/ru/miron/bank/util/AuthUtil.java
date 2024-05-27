package ru.miron.bank.util;

import org.springframework.security.core.Authentication;

public class AuthUtil {
    public static String getLogin(Authentication authentication) {
        return authentication.getName();
    }
}
