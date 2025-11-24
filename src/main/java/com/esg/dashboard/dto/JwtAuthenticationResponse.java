package com.esg.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа с JWT токеном
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с JWT токеном аутентификации")
public class JwtAuthenticationResponse {

    @Schema(description = "JWT токен доступа", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Тип токена", example = "Bearer")
    @lombok.Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Время истечения токена в миллисекундах", example = "86400000")
    private Long expiresIn;
}

