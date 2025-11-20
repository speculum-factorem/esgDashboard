package com.esg.dashboard.exception;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(String companyId) {
        super("Company not found with ID: " + companyId);
    }

    public CompanyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}