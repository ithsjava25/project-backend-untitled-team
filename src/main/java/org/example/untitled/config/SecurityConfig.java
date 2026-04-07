package org.example.untitled.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        //change later to authenticated
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/images/**", "/css/**").permitAll()
                .anyRequest().permitAll()
        );

        return http.build();
    }
}
