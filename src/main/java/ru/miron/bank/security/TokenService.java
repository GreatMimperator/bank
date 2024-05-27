package ru.miron.bank.security;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.miron.bank.api.dto.AccessAndRefreshTokenDto;
import ru.miron.bank.security.entity.WorkingRefreshToken;
import ru.miron.bank.security.repository.WorkingRefreshTokenRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final WorkingRefreshTokenRepository workingRefreshTokenRepository;
    private final EntityManager entityManager;

    public TokenService(JwtEncoder jwtEncoder,
                        WorkingRefreshTokenRepository workingRefreshTokenRepository,
                        EntityManager entityManager) {
        this.jwtEncoder = jwtEncoder;
        this.workingRefreshTokenRepository = workingRefreshTokenRepository;
        this.entityManager = entityManager;
    }

    @Value("${jwt.accessToken.expiresInSeconds}")
    private Long accessTokenExpiresInSeconds;
    @Value("${jwt.refreshToken.expiresInSeconds}")
    private Long refreshTokenExpiresInSeconds;

    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> authorities) {
        var now = Instant.now();
        var expiresAt = now.plusSeconds(accessTokenExpiresInSeconds);
        var scope = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .claim("scope", scope)
                .build();
        return claimsToJwt(claims);
    }

    @Transactional
    public String generateRefreshToken(String username, Optional<Jwt> previousRefreshToken) {
        var now = Instant.now();
        var expiresAt = now.plusSeconds(refreshTokenExpiresInSeconds);
        var uuid = generateDashlessUUID();
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .id(uuid)
                .build();
        if (previousRefreshToken.isPresent()) {
            workingRefreshTokenRepository.deleteById(previousRefreshToken.get().getId());
            entityManager.flush();
        }
        workingRefreshTokenRepository.save(new WorkingRefreshToken(uuid, username));
        return claimsToJwt(claims);
    }

    @Transactional
    public boolean isWorkingRefreshToken(Jwt refreshToken) {
        return workingRefreshTokenRepository.existsById(refreshToken.getId());
    }

    @Transactional
    public void logout(Jwt refreshToken) {
        workingRefreshTokenRepository.deleteByLogin(refreshToken.getSubject());
    }

    private String claimsToJwt(JwtClaimsSet claims) {
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String generateDashlessUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}