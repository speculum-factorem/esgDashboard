package com.esg.dashboard.exception;

import com.esg.dashboard.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCompanyNotFound(CompanyNotFoundException ex) {
        log.warn("Company not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(PortfolioNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handlePortfolioNotFound(PortfolioNotFoundException ex) {
        log.warn("Portfolio not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataValidation(DataValidationException ex) {
        log.warn("Data validation error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKey(org.springframework.dao.DuplicateKeyException ex) {
        log.warn("Duplicate key error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Resource already exists"));
    }
}