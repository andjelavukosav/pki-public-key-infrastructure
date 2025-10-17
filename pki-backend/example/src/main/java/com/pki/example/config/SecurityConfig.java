package com.pki.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// NOVI IMPORT: Za ručno kreiranje RequestMatcher-a
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Definišemo Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Omogućava CORS podršku. Koristi bean corsConfigurationSource() definisan ispod.
                .cors(Customizer.withDefaults())

                // Onemogućite CSRF
                .csrf(csrf -> csrf.disable())

                // Primer autorizacije
                .authorizeHttpRequests(auth -> auth
                        // REŠENJE: Korišćenje AntPathRequestMatcher
                        // Kreiramo RequestMatcher za putanju /api/auth/**
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()

                        // Primer dozvole za statičke resurse (opciono):
                        // .requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()

                        .anyRequest().authenticated() // Za sve ostale zahteve potrebna je autentifikacija
                );

        return http.build();
    }

    // Definišemo CORS pravila
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("*"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
