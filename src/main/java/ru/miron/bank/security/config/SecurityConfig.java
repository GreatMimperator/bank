package ru.miron.bank.security.config;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("");
        var authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(converter);
        return authenticationConverter;
    }

    @Bean("jwtTokenProvider")
    public AuthenticationProvider jwtTokenProvider(JwtDecoder jwtDecoder, JwtAuthenticationConverter jwtAuthenticationConverter) {
        var jwtLockableAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        jwtLockableAuthenticationProvider.setJwtAuthenticationConverter(jwtAuthenticationConverter);
        return jwtLockableAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authManager(@Qualifier("jwtTokenProvider") AuthenticationProvider jwtTokenProvider) {
        return new ProviderManager(jwtTokenProvider);
    }

    @Bean
    public BearerTokenAuthenticationFilter bearerTokenAuthenticationToken(AuthenticationManager authenticationManager) {
        return new BearerTokenAuthenticationFilter(authenticationManager);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, BearerTokenAuthenticationFilter bearerTokenAuthenticationFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((authorizeHttpRequest) ->
                authorizeHttpRequest
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/service/**").permitAll()
                        .anyRequest().authenticated());

        http.addFilterBefore(
                bearerTokenAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }
}