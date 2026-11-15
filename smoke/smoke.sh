#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:?Set BASE_URL, e.g. http://localhost:8080}"
EXPECTED_VERSION="${EXPECTED_VERSION:-}"
PASS=0
FAIL=0

check() {
    local name="$1" url="$2" expected_status="$3" expected_body="${4:-}"
    local status body

    body=$(curl -s -w "\n%{http_code}" --max-time 10 "$url")
    status=$(echo "$body" | tail -1)
    body=$(echo "$body" | sed '$d')

    if [[ "$status" != "$expected_status" ]]; then
        echo "FAIL  $name  (expected $expected_status, got $status)"
        FAIL=$((FAIL + 1))
        return
    fi

    if [[ -n "$expected_body" ]] && ! echo "$body" | grep -q "$expected_body"; then
        echo "FAIL  $name  (body missing: $expected_body)"
        FAIL=$((FAIL + 1))
        return
    fi

    echo "PASS  $name"
    PASS=$((PASS + 1))
}

echo "=== Smoke test: $BASE_URL ==="
echo ""

check "Liveness probe"    "$BASE_URL/actuator/health/liveness"  200 '"status":"UP"'
check "Readiness probe"   "$BASE_URL/actuator/health/readiness" 200 '"status":"UP"'
check "API /info"         "$BASE_URL/api/info"                  200 '"hostname"'
check "API /info (time)"  "$BASE_URL/api/info"                  200 '"currentTimeUtc"'
check "Page /"            "$BASE_URL/"                          200 "Host Info Dashboard"
check "Version"           "$BASE_URL/version"                   200 '"version"'

if [[ -n "$EXPECTED_VERSION" ]]; then
    check "Version match" "$BASE_URL/version" 200 "\"version\":\"$EXPECTED_VERSION\""
fi

echo ""
echo "=== Results: $PASS passed, $FAIL failed ==="

if [[ $FAIL -gt 0 ]]; then
    exit 1
fi
