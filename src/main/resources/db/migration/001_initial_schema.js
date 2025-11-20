// MongoDB initial schema migration
print('Starting ESG Dashboard database migration...');

// Create collections
db.createCollection('companies');
db.createCollection('portfolios');
db.createCollection('historical_data');
db.createCollection('audit_logs');
db.createCollection('esg_events');

// Create indexes for companies
db.companies.createIndex({ "companyId": 1 }, { unique: true, name: "company_id_unique" });
db.companies.createIndex({ "sector": 1 }, { name: "sector_index" });
db.companies.createIndex({ "currentRating.overallScore": -1 }, { name: "overall_score_index" });
db.companies.createIndex({ "currentRating.ranking": 1 }, { name: "ranking_index" });
db.companies.createIndex({ "updatedAt": -1 }, { name: "updated_at_index" });

// Create indexes for portfolios
db.portfolios.createIndex({ "portfolioId": 1 }, { unique: true, name: "portfolio_id_unique" });
db.portfolios.createIndex({ "clientId": 1 }, { name: "client_id_index" });
db.portfolios.createIndex({ "aggregateScores.totalEsgScore": -1 }, { name: "portfolio_score_index" });

// Create indexes for historical data
db.historical_data.createIndex({ "companyId": 1, "recordDate": -1 }, { name: "company_history_index" });
db.historical_data.createIndex({ "dataType": 1 }, { name: "data_type_index" });
db.historical_data.createIndex({ "recordDate": 1 }, { name: "record_date_index" });

// Create indexes for audit logs
db.audit_logs.createIndex({ "timestamp": -1 }, { name: "audit_timestamp_index" });
db.audit_logs.createIndex({ "entityType": 1, "entityId": 1 }, { name: "audit_entity_index" });
db.audit_logs.createIndex({ "userId": 1 }, { name: "audit_user_index" });

// Create indexes for events
db.esg_events.createIndex({ "timestamp": -1 }, { name: "events_timestamp_index" });
db.esg_events.createIndex({ "companyId": 1, "eventType": 1 }, { name: "events_company_index" });

print('Database migration completed successfully!');