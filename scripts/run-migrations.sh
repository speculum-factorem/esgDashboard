#!/bin/bash

echo "Running ESG Dashboard database migrations..."

# Check if MongoDB is running
if ! mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; then
    echo "MongoDB is not running. Please start MongoDB first."
    exit 1
fi

# Run initial migration
echo "Executing initial schema migration..."
mongosh esg-dashboard src/main/resources/db/migration/001_initial_schema.js

# Verify migration
echo "Verifying migration..."
mongosh esg-dashboard --eval "
print('Collections created:');
db.getCollectionNames().forEach(function(collection) {
    print('- ' + collection);
});

print('\nIndexes created:');
db.getCollectionInfos().forEach(function(coll) {
    print('Collection: ' + coll.name);
    db[coll.name].getIndexes().forEach(function(idx) {
        print('  - ' + idx.name);
    });
});
"

echo "Database migrations completed successfully!"