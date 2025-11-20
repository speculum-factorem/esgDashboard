package com.esg.dashboard.component;

import com.esg.dashboard.model.Company;
import com.esg.dashboard.model.ESGRating;
import com.esg.dashboard.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final CompanyService companyService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample ESG data...");

        List<Company> sampleCompanies = List.of(
                createCompany("TECH001", "GreenTech Solutions", "Technology", 88.5, 95.0, 85.0, 85.5, 50.2, 82.0, "AA"),
                createCompany("ENERGY001", "EcoEnergy Corp", "Energy", 92.0, 98.0, 88.0, 90.0, 25.8, 90.0, "AAA"),
                createCompany("FIN001", "Sustainable Finance Ltd", "Finance", 76.5, 70.0, 85.0, 74.5, 80.1, 83.0, "A"),
                createCompany("HEALTH001", "BioHealth Innovations", "Healthcare", 81.0, 75.0, 88.0, 80.0, 65.3, 86.0, "AA"),
                createCompany("MANUF001", "EcoManufacturing Inc", "Manufacturing", 69.5, 85.0, 65.0, 58.5, 110.2, 62.0, "BBB")
        );

        for (Company company : sampleCompanies) {
            try {
                companyService.saveOrUpdateCompany(company);
                log.debug("Sample company created: {}", company.getName());
            } catch (Exception e) {
                log.warn("Failed to create sample company {}: {}", company.getName(), e.getMessage());
            }
        }

        log.info("Sample data initialization completed");
    }

    private Company createCompany(String companyId, String name, String sector,
                                  double overall, double env, double social, double gov,
                                  double carbon, double socialImpact, String grade) {
        ESGRating rating = ESGRating.builder()
                .overallScore(overall)
                .environmentalScore(env)
                .socialScore(social)
                .governanceScore(gov)
                .carbonFootprint(carbon)
                .socialImpactScore(socialImpact)
                .ratingGrade(grade)
                .calculationDate(LocalDateTime.now())
                .build();

        return Company.builder()
                .companyId(companyId)
                .name(name)
                .sector(sector)
                .currentRating(rating)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}