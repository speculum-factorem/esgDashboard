#!/bin/bash

# Kubernetes health check script for ESG Dashboard

HEALTH_URL="http://localhost:8080/actuator/health"
READINESS_URL="http://localhost:8080/actuator/health/readiness"
LIVENESS_URL="http://localhost:8080/actuator/health/liveness"

# Function to check health endpoint
check_health() {
    local url=$1
    local endpoint=$2

    response=$(curl -s -w "%{http_code}" -o /tmp/health_response $url)

    if [ "$response" -eq 200 ]; then
        echo "$endpoint health check: PASSED"
        return 0
    else
        echo "$endpoint health check: FAILED (HTTP $response)"
        return 1
    fi
}

# Main health check
echo "Starting ESG Dashboard health check..."

# Check application health
if ! check_health $HEALTH_URL "Application"; then
    exit 1
fi

# Check readiness
if ! check_health $READINESS_URL "Readiness"; then
    exit 1
fi

# Check liveness
if ! check_health $LIVENESS_URL "Liveness"; then
    exit 1
fi

echo "All health checks passed successfully!"
exit 0