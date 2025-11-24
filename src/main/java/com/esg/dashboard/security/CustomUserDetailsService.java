package com.esg.dashboard.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Сервис для загрузки пользователей для Spring Security
 * В production версии должен загружать пользователей из базы данных
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    // В production версии здесь должна быть интеграция с базой данных пользователей
    // Пока используем in-memory пользователей для демонстрации

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user: {}", username);

        // Демонстрационные пользователи
        // В production версии загружать из базы данных
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pL5O") // password: admin
                    .authorities("ROLE_ADMIN", "ROLE_USER")
                    .build();
        } else if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pL5O") // password: user
                    .authorities("ROLE_USER")
                    .build();
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}

