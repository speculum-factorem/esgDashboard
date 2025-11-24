package com.esg.dashboard.repository;

import com.esg.dashboard.model.HistoricalData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricalDataRepository extends MongoRepository<HistoricalData, String> {

    List<HistoricalData> findByCompanyIdAndDataTypeOrderByRecordDateDesc(String companyId, String dataType);
    
    Page<HistoricalData> findByCompanyIdOrderByRecordDateDesc(String companyId, Pageable pageable);
    
    Page<HistoricalData> findByCompanyIdAndDataTypeOrderByRecordDateDesc(String companyId, String dataType, Pageable pageable);

    @Query("{ 'companyId': ?0, 'dataType': ?1, 'recordDate': { $gte: ?2, $lte: ?3 } }")
    List<HistoricalData> findByCompanyIdAndDataTypeAndDateRange(
            String companyId, String dataType, LocalDateTime start, LocalDateTime end);
    
    @Query("{ 'companyId': ?0, 'dataType': ?1, 'recordDate': { $gte: ?2, $lte: ?3 } }")
    Page<HistoricalData> findByCompanyIdAndDataTypeAndDateRange(
            String companyId, String dataType, LocalDateTime start, LocalDateTime end, Pageable pageable);

    void deleteByCompanyId(String companyId);

    List<HistoricalData> findTop10ByCompanyIdOrderByRecordDateDesc(String companyId);
}