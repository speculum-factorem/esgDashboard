package com.esg.dashboard.service;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.Portfolio;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
            MDC.put("operation", "EXPORT_COMPANIES_JSON");
            log.info("Exporting companies to JSON format");

            List<Company> companies = mongoTemplate.find(new Query(), Company.class);

            ObjectMapper exportMapper = new ObjectMapper();
            exportMapper.registerModule(new JavaTimeModule());
            exportMapper.enable(SerializationFeature.INDENT_OUTPUT);

            StringWriter writer = new StringWriter();
            exportMapper.writeValue(writer, companies);

            log.info("Exported {} companies to JSON", companies.size());
            return writer.toString();

        } catch (Exception e) {
            log.error("Failed to export companies: {}", e.getMessage(), e);
            throw new RuntimeException("Export failed", e);
        } finally {
            MDC.clear();
        }
    }

    public String exportPortfoliosToJson() {
        try {
            MDC.put("operation", "EXPORT_PORTFOLIOS_JSON");
            log.info("Exporting portfolios to JSON format");

            List<Portfolio> portfolios = mongoTemplate.find(new Query(), Portfolio.class);

            ObjectMapper exportMapper = new ObjectMapper();
            exportMapper.registerModule(new JavaTimeModule());
            exportMapper.enable(SerializationFeature.INDENT_OUTPUT);

            StringWriter writer = new StringWriter();
            exportMapper.writeValue(writer, portfolios);

            log.info("Exported {} portfolios to JSON", portfolios.size());
            return writer.toString();

        } catch (Exception e) {
            log.error("Failed to export portfolios: {}", e.getMessage(), e);
            throw new RuntimeException("Export failed", e);
        } finally {
            MDC.clear();
        }
    }

    public String generateExportFilename(String prefix) {
        // Генерация имени файла с временной меткой
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_export_%s.json", prefix, timestamp);
    }

    public byte[] exportCompaniesToCsv() {
        try {
            MDC.put("operation", "EXPORT_COMPANIES_CSV");
            log.info("Exporting companies to CSV format");

            List<Company> companies = mongoTemplate.find(new Query(), Company.class);

            StringBuilder csv = new StringBuilder();
            // Заголовок CSV
            csv.append("Company ID,Name,Sector,Overall Score,Environmental Score,Social Score,Governance Score,Carbon Footprint,Social Impact,Rating Grade\n");

            // Данные компаний
            for (Company company : companies) {
                if (company.getCurrentRating() != null) {
                    csv.append(String.format("\"%s\",\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,\"%s\"\n",
                            company.getCompanyId() != null ? company.getCompanyId() : "",
                            company.getName() != null ? company.getName() : "",
                            company.getSector() != null ? company.getSector() : "",
                            company.getCurrentRating().getOverallScore() != null ? company.getCurrentRating().getOverallScore() : 0.0,
                            company.getCurrentRating().getEnvironmentalScore() != null ? company.getCurrentRating().getEnvironmentalScore() : 0.0,
                            company.getCurrentRating().getSocialScore() != null ? company.getCurrentRating().getSocialScore() : 0.0,
                            company.getCurrentRating().getGovernanceScore() != null ? company.getCurrentRating().getGovernanceScore() : 0.0,
                            company.getCurrentRating().getCarbonFootprint() != null ? company.getCurrentRating().getCarbonFootprint() : 0.0,
                            company.getCurrentRating().getSocialImpactScore() != null ? company.getCurrentRating().getSocialImpactScore() : 0.0,
                            company.getCurrentRating().getRatingGrade() != null ? company.getCurrentRating().getRatingGrade() : "N/A"));
                }
            }

            log.info("Exported {} companies to CSV", companies.size());
            return csv.toString().getBytes();

        } catch (Exception e) {
            log.error("Failed to export companies to CSV: {}", e.getMessage(), e);
            throw new RuntimeException("CSV export failed", e);
        } finally {
            MDC.clear();
        }
    }
}