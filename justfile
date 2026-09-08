# "sh" not "bash": Windows ships a `bash.exe` stub (System32 + WindowsApps alias)
# that silently redirects to WSL, and WSL's Windows->Linux argv marshalling mangles
# multi-statement recipes with $(...) captures. There is no equivalent `sh.exe` stub,
# so this name only resolves via a real shell (e.g. Git for Windows) on every OS.
set shell := ["sh", "-cu"]

# Build and start product-service and order-service, waiting for them to become healthy.
up:
    docker compose up -d --build --wait

# Build and start the stack with N instances of product-service, waiting for them to become healthy.
up-scaled n:
    docker compose up -d --build --wait --scale product-service={{n}}

# Stop and remove the infrastructure.
down:
    docker compose down

# Bring up the infrastructure and verify the order-service -> product-service integration works.
# No shebang: on Windows, just needs `cygpath` (from Git for Windows) on PATH to run
# shebang recipes, which usually isn't on PATH. Backslash-continuations keep this a
# single recipe line so response/status/body persist across statements without one.
smoke: up
    response=$(curl -s -w "\n%{http_code}" -X POST http://localhost:8082/orders \
        -H "Content-Type: application/json" \
        -d '{"productId":1,"quantity":2}'); \
    status=$(printf '%s' "$response" | tail -n1); \
    body=$(printf '%s' "$response" | sed '$d'); \
    echo "$body"; \
    if [ "$status" != "201" ]; then \
        echo "Smoke test FAILED: expected HTTP 201, got $status" >&2; \
        exit 1; \
    fi; \
    echo "Smoke test passed: order created (201)"
