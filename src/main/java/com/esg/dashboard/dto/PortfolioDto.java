package com.esg.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {
    private String id;

    @NotBlank(message = "Portfolio ID is required")
    private String portfolioId;

    @NotBlank(message = "Portfolio name is required")
    private String portfolioName;

    @NotBlank(message = "Client ID is required")
    private String clientId;

    private String clientName;

    @NotEmpty(message = "Portfolio items cannot be empty")
    private List<@Valid PortfolioItemDto> items;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PortfolioItemDto {
    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotNull(message = "Investment amount is required")
    private Double investmentAmount;
}