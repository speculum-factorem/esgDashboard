package com.esg.dashboard.exception;

public class PortfolioNotFoundException extends RuntimeException {

    public PortfolioNotFoundException(String portfolioId) {
        super("Portfolio not found with ID: " + portfolioId);
    }

    public PortfolioNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}