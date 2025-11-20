// MongoDB initialization script
db.createCollection('companies');
db.createCollection('portfolios');

db.companies.createIndex({ "companyId": 1 }, { unique: true });
db.companies.createIndex({ "sector": 1 });
db.companies.createIndex({ "currentRating.overallScore": -1 });

db.portfolios.createIndex({ "portfolioId": 1 }, { unique: true });
db.portfolios.createIndex({ "clientId": 1 });

print('ESG Dashboard MongoDB initialized successfully');