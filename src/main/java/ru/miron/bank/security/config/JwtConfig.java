package ru.miron.bank.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import ru.miron.bank.security.RsaProperties;

@Configuration
@EnableConfigurationProperties({RsaProperties.class})
@AllArgsConstructor
public class JwtConfig {

    private final RsaProperties rsaProperties;

    @Bean
    public JwtEncoder jwtEncoder() {
        var jwk = new RSAKey.Builder(rsaProperties.getPublicKey())
                .privateKey(rsaProperties.getPrivateKey())
                .build();
        var jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaProperties.getPublicKey()).build();
    }
}