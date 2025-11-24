package com.esg.dashboard.repository;

import com.esg.dashboard.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findByCompanyId(String companyId);

    List<Company> findBySector(String sector);
    
    Page<Company> findBySector(String sector, Pageable pageable);

    @Query("{ 'currentRating.overallScore': { $gte: ?0 } }")
    List<Company> findByOverallScoreGreaterThanEqual(Double minScore);
    
    @Query("{ 'currentRating.overallScore': { $gte: ?0 } }")
    Page<Company> findByOverallScoreGreaterThanEqual(Double minScore, Pageable pageable);

    @Query("{ 'currentRating.ranking': { $lte: ?0 } }")
    List<Company> findTopRankedCompanies(int limit);
    
    @Query(value = "{ 'currentRating.overallScore': { $exists: true } }", sort = "{ 'currentRating.overallScore': -1 }")
    Page<Company> findTopRankedCompanies(Pageable pageable);

    List<Company> findByCompanyIdIn(List<String> companyIds);
}