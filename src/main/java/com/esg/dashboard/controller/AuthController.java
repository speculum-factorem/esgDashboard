package com.esg.dashboard.controller;

import com.esg.dashboard.dto.ApiResponse;
import com.esg.dashboard.dto.JwtAuthenticationResponse;
import com.esg.dashboard.dto.LoginRequest;
import com.esg.dashboard.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для аутентификации и получения JWT токенов
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для аутентификации и получения JWT токенов")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    @Value("${app.jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @PostMapping("/login")
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Аутентифицирует пользователя и возвращает JWT токен"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Аутентификация успешна"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> login(
            @Parameter(description = "Учетные данные пользователя", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            MDC.put("operation", "LOGIN");
            MDC.put("username", loginRequest.getUsername());
            log.info("Authentication attempt for user: {}", loginRequest.getUsername());

            // Аутентификация пользователя
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Загрузка пользователя и генерация токена
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = tokenProvider.generateToken(userDetails);

            JwtAuthenticationResponse response = JwtAuthenticationResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtExpirationMs)
                    .build();

            log.info("Authentication successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Authentication failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Invalid username or password"));
        } finally {
            MDC.clear();
        }
    }
}

