package ru.miron.bank.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Data
@ConfigurationProperties(prefix = "jwt")
@Component
public class RsaProperties {
    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;
}