set shell := ["bash", "-cu"]

# Build and start product-service and order-service, waiting for them to become healthy.
up:
    docker compose up -d --build --wait

# Stop and remove the infrastructure.
down:
    docker compose down

# Bring up the infrastructure and verify the order-service -> product-service integration works.
smoke: up
    #!/usr/bin/env bash
    set -euo pipefail
    response=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8082/orders \
        -H "Content-Type: application/json" \
        -d '{"productId":1,"quantity":2}')
    status=$(printf '%s' "$response" | tail -n1)
    body=$(printf '%s' "$response" | sed '$d')
    echo "$body"
    if [ "$status" != "201" ]; then
        echo "Smoke test FAILED: expected HTTP 201, got $status" >&2
        exit 1
    fi
    echo "Smoke test passed: order created (201)"
