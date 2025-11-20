package com.esg.dashboard.controller;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.service.CompanyService;
import com.esg.dashboard.service.RealTimeUpdateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final RealTimeUpdateService realTimeUpdateService;

    @PostMapping
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        try {
            MDC.put("companyId", company.getCompanyId());
            log.info("Creating new company: {}", company.getName());

            Company savedCompany = companyService.saveOrUpdateCompany(company);
            realTimeUpdateService.publishCompanyUpdate(savedCompany);

            return ResponseEntity.ok(savedCompany);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompany(@PathVariable String companyId) {
        try {
            MDC.put("companyId", companyId);
            log.debug("Fetching company: {}", companyId);

            return companyService.findByCompanyId(companyId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{companyId}/rating")
    public ResponseEntity<Company> updateRating(
            @PathVariable String companyId,
            @Valid @RequestBody ESGRating newRating) {
        try {
            MDC.put("companyId", companyId);
            log.info("Updating rating for company: {}", companyId);

            companyService.updateESGRating(companyId, newRating);
            realTimeUpdateService.publishRatingUpdate(companyId, newRating);

            return getCompany(companyId);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/top-ranked")
    public ResponseEntity<List<Company>> getTopRankedCompanies(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} ranked companies", limit);

        List<Company> topCompanies = companyService.getTopRankedCompanies(limit);
        return ResponseEntity.ok(topCompanies);
    }

    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Company>> getCompaniesBySector(@PathVariable String sector) {
        log.info("Fetching companies in sector: {}", sector);

        // This would require additional service method
        return ResponseEntity.ok(List.of());
    }
}