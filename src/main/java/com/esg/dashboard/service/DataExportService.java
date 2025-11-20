package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.Portfolio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataExportService {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public String exportCompaniesToJson() {
        try {
            List<Company> companies = mongoTemplate.find(new Query(), Company.class);

            ObjectMapper exportMapper = new ObjectMapper();
            exportMapper.registerModule(new JavaTimeModule());
            exportMapper.enable(SerializationFeature.INDENT_OUTPUT);

            StringWriter writer = new StringWriter();
            exportMapper.writeValue(writer, companies);

            log.info("Exported {} companies to JSON", companies.size());
            return writer.toString();

        } catch (Exception e) {
            log.error("Failed to export companies: {}", e.getMessage());
            throw new RuntimeException("Export failed", e);
        }
    }

    public String exportPortfoliosToJson() {
        try {
            List<Portfolio> portfolios = mongoTemplate.find(new Query(), Portfolio.class);

            ObjectMapper exportMapper = new ObjectMapper();
            exportMapper.registerModule(new JavaTimeModule());
            exportMapper.enable(SerializationFeature.INDENT_OUTPUT);

            StringWriter writer = new StringWriter();
            exportMapper.writeValue(writer, portfolios);

            log.info("Exported {} portfolios to JSON", portfolios.size());
            return writer.toString();

        } catch (Exception e) {
            log.error("Failed to export portfolios: {}", e.getMessage());
            throw new RuntimeException("Export failed", e);
        }
    }

    public String generateExportFilename(String prefix) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_export_%s.json", prefix, timestamp);
    }

    public byte[] exportCompaniesToCsv() {
        try {
            List<Company> companies = mongoTemplate.find(new Query(), Company.class);

            StringBuilder csv = new StringBuilder();
            // Header
            csv.append("Company ID,Name,Sector,Overall Score,Environmental Score,Social Score,Governance Score,Carbon Footprint,Social Impact,Rating Grade\n");

            // Data
            for (Company company : companies) {
                csv.append(String.format("\"%s\",\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,\"%s\"\n",
                        company.getCompanyId(),
                        company.getName(),
                        company.getSector(),
                        company.getCurrentRating().getOverallScore(),
                        company.getCurrentRating().getEnvironmentalScore(),
                        company.getCurrentRating().getSocialScore(),
                        company.getCurrentRating().getGovernanceScore(),
                        company.getCurrentRating().getCarbonFootprint(),
                        company.getCurrentRating().getSocialImpactScore(),
                        company.getCurrentRating().getRatingGrade()));
            }

            log.info("Exported {} companies to CSV", companies.size());
            return csv.toString().getBytes();

        } catch (Exception e) {
            log.error("Failed to export companies to CSV: {}", e.getMessage());
            throw new RuntimeException("CSV export failed", e);
        }
    }
}