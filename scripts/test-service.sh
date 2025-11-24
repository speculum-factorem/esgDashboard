#!/bin/bash

# Test script for ESG Dashboard service
# This script tests the main endpoints of the service

BASE_URL="http://localhost:8080"
API_BASE="${BASE_URL}/api/v1"

echo "========================================="
echo "ESG Dashboard Service Test"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test function
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -n "Testing: $description... "
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" -X GET "${API_BASE}${endpoint}")
    elif [ "$method" = "POST" ]; then
        response=$(curl -s -w "\n%{http_code}" -X POST "${API_BASE}${endpoint}" \
            -H "Content-Type: application/json" \
            -d "$data")
    elif [ "$method" = "PUT" ]; then
        response=$(curl -s -w "\n%{http_code}" -X PUT "${API_BASE}${endpoint}" \
            -H "Content-Type: application/json" \
            -d "$data")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $http_code)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $http_code)"
        echo "Response: $body"
        return 1
    fi
}

# Check if service is running
echo "Checking if service is running..."
if ! curl -s -f "${BASE_URL}/actuator/health" > /dev/null; then
    echo -e "${RED}Error: Service is not running on ${BASE_URL}${NC}"
    echo "Please start the service first:"
    echo "  ./gradlew bootRun"
    exit 1
fi

echo -e "${GREEN}Service is running${NC}"
echo ""

# Test health endpoint
test_endpoint "GET" "" "/health" "Health Check"

# Test creating a company
COMPANY_DATA='{
  "companyId": "TEST001",
  "name": "Test Company",
  "sector": "Technology",
  "industry": "Software",
  "currentRating": {
    "overallScore": 85.5,
    "environmentalScore": 90.0,
    "socialScore": 80.0,
    "governanceScore": 86.5,
    "carbonFootprint": 120.5,
    "socialImpactScore": 78.0,
    "ratingGrade": "AA"
  }
}'

test_endpoint "POST" "/companies" "$COMPANY_DATA" "Create Company"

# Test getting company
test_endpoint "GET" "/companies/TEST001" "" "Get Company by ID"

# Test getting companies by sector
test_endpoint "GET" "/companies/sector/Technology" "" "Get Companies by Sector"

# Test getting top ranked companies
test_endpoint "GET" "/companies/top-ranked?limit=10" "" "Get Top Ranked Companies"

# Test updating rating
RATING_DATA='{
  "overallScore": 90.0,
  "environmentalScore": 95.0,
  "socialScore": 85.0,
  "governanceScore": 90.0,
  "carbonFootprint": 100.0,
  "socialImpactScore": 85.0,
  "ratingGrade": "AAA"
}'

test_endpoint "PUT" "/companies/TEST001/rating" "$RATING_DATA" "Update ESG Rating"

# Test system info
test_endpoint "GET" "/system/info" "" "Get System Info"

# Test cache stats
test_endpoint "GET" "/system/cache/stats" "" "Get Cache Stats"

echo ""
echo "========================================="
echo "Tests completed"
echo "========================================="

