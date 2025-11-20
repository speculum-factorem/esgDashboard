package com.esg.dashboard.repository;

import com.esg.dashboard.model.Portfolio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends MongoRepository<Portfolio, String> {

    Optional<Portfolio> findByPortfolioId(String portfolioId);

    List<Portfolio> findByClientId(String clientId);

    @Query("{ 'clientId': ?0, 'portfolioName': { $regex: ?1, $options: 'i' } }")
    List<Portfolio> findByClientIdAndPortfolioNameLike(String clientId, String portfolioName);

    boolean existsByPortfolioId(String portfolioId);
}