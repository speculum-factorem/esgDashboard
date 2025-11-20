package com.esg.dashboard.repository;

import com.esg.dashboard.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findByCompanyId(String companyId);

    List<Company> findBySector(String sector);

    @Query("{ 'currentRating.overallScore': { $gte: ?0 } }")
    List<Company> findByOverallScoreGreaterThanEqual(Double minScore);

    @Query("{ 'currentRating.ranking': { $lte: ?0 } }")
    List<Company> findTopRankedCompanies(int limit);
}