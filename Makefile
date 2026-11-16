# ==============================================================================
# Dynamic Environment-Driven Makefile
# ==============================================================================
BUILD_NUMBER ?= local
VERSION      ?= 1.0.$(BUILD_NUMBER)
GROUP_ID     ?= com/example
ARTIFACT_ID  ?= host-info

# Local Environment Configuration
DB_NAME_DEV  ?= hostinfo_dev
DB_NAME_PROD ?= hostinfo_prod
SERVER_PORT  ?= 8080
APP_HOST     ?= localhost

# Derived Artifact Path
ARTIFACT_PATH = $(GROUP_ID)/$(ARTIFACT_ID)/$(VERSION)/$(ARTIFACT_ID)-$(VERSION).jar

.PHONY: all check-jfrog check-db set-version test-unit test-integration test-e2e \
	test-all build publish-candidate deploy-dev smoke-dev promote-prod \
	deploy-prod smoke-prod rollback-prod clean

# Default target
all: test-all

# ==============================================================================
# Helper Validation Targets (Fails fast if env vars are missing)
# ==============================================================================
check-jfrog:
	@if [ -z "$(JFROG_URL)" ]; then echo "ERROR: JFROG_URL is not set"; exit 1; fi
	@if [ -z "$(JFROG_USER)" ]; then echo "ERROR: JFROG_USER is not set"; exit 1; fi
	@if [ -z "$(JFROG_PASS)" ]; then echo "ERROR: JFROG_PASS is not set"; exit 1; fi

check-db:
	@if [ -z "$(DB_HOST)" ]; then echo "ERROR: DB_HOST is not set"; exit 1; fi
	@if [ -z "$(DB_USER)" ]; then echo "ERROR: DB_USER is not set"; exit 1; fi
	@if [ -z "$(DB_PASS)" ]; then echo "ERROR: DB_PASS is not set"; exit 1; fi

# ==============================================================================
# Stage 0: Versioning
# ==============================================================================
set-version:
	@echo "===> Setting POM version to $(VERSION)"
	mvn versions:set -DnewVersion=$(VERSION) -DgenerateBackupPoms=false

# ==============================================================================
# Stage 1: Granular Test Pyramid Targets
# ==============================================================================
test-unit:
	@echo "===> Running Unit Tests (Fast Feedback)"
	mvn clean test -Dgroups="unit"

test-integration: check-db
	@echo "===> Running Integration Tests (DB Wiring against $(DB_HOST))"
	mvn test -Dgroups="integration"

test-e2e: check-db
	@echo "===> Running E2E Tests (Full Stack)"
	mvn verify -Dgroups="e2e"

test-all: test-unit test-integration test-e2e

# ==============================================================================
# Stage 2: Packaging & Artifact Management
# ==============================================================================
build: set-version
	@echo "===> Packaging Fat JAR"
	mvn clean package -DskipTests

publish-candidate: check-jfrog set-version
	@echo "===> Publishing Release Artifact to $(JFROG_URL)/candidate_repo"
	mvn deploy -DskipTests

promote-prod: check-jfrog
	@echo "===> Promoting Version $(VERSION) from candidate_repo to production_repo (OSS Mode)"
	@mkdir -p /tmp/promote
	@echo "1/2 Downloading from candidate_repo..."
	curl -u $(JFROG_USER):$(JFROG_PASS) -s -f -o /tmp/promote/$(ARTIFACT_ID)-$(VERSION).jar \
		"$(JFROG_URL)/candidate_repo/$(ARTIFACT_PATH)"
	@echo "2/2 Uploading exact binary to production_repo..."
	curl -u $(JFROG_USER):$(JFROG_PASS) -f -T /tmp/promote/$(ARTIFACT_ID)-$(VERSION).jar \
		"$(JFROG_URL)/production_repo/$(ARTIFACT_PATH)"
	@rm -rf /tmp/promote
	@echo "===> Successfully promoted Version $(VERSION) to production_repo!"

# ==============================================================================
# Stage 3: Direct Dev Deployment & Smoke
# ==============================================================================
deploy-dev: check-jfrog
	@echo "===> Deploying Version $(VERSION) directly on current host (DEV Profile)"
	pkill -f '[h]ost-info.*dev' || true
	mkdir -p /opt/app
	curl -u $(JFROG_USER):$(JFROG_PASS) -s -o /opt/app/host-info.jar \
		$(JFROG_URL)/candidate_repo/$(ARTIFACT_PATH)
	nohup java -jar /opt/app/host-info.jar --spring.profiles.active=dev > /var/log/host-info-dev.log 2>&1 &
	@echo "===> Waiting 15s for Dev startup..."
	@sleep 45

smoke-dev:
	@echo "===> Running Post-Deployment Smoke Test against DEV"
	BASE_URL=http://$(APP_HOST):$(SERVER_PORT) EXPECTED_VERSION=$(VERSION) ./smoke/smoke.sh

# ==============================================================================
# Stage 4: Direct Prod Deployment & Smoke
# ==============================================================================
deploy-prod: check-jfrog check-db
	@echo "===> Deploying Version $(VERSION) directly on current host (PROD Profile)"
	pkill -f '[h]ost-info.*prod' || true
	mkdir -p /opt/app
	curl -u $(JFROG_USER):$(JFROG_PASS) -s -o /opt/app/host-info.jar \
		$(JFROG_URL)/production_repo/$(ARTIFACT_PATH)
	nohup java -jar /opt/app/host-info.jar \
		--spring.profiles.active=prod \
		-DSPRING_DATASOURCE_URL=jdbc:postgresql://$(DB_HOST):5432/$(DB_NAME_PROD) \
		-DSPRING_DATASOURCE_USERNAME=$(DB_USER) \
		-DSPRING_DATASOURCE_PASSWORD=$(DB_PASS) \
		-DSERVER_PORT=$(SERVER_PORT) \
		> /var/log/host-info-prod.log 2>&1 &
	@echo "===> Waiting 15s for Prod startup..."
	@sleep 45

smoke-prod:
	@echo "===> Running Post-Deployment Smoke Test against PROD"
	BASE_URL=http://$(APP_HOST):$(SERVER_PORT) EXPECTED_VERSION=$(VERSION) ./smoke/smoke.sh

# ==============================================================================
# Emergency Operations: Direct Rollback
# Usage: make rollback-prod TARGET_VERSION=1.0.41
# ==============================================================================
rollback-prod: check-jfrog check-db
	@if [ -z "$(TARGET_VERSION)" ]; then \
		echo "ERROR: TARGET_VERSION is required. Example: make rollback-prod TARGET_VERSION=1.0.41"; \
		exit 1; \
	fi
	@echo "===> EMERGENCY ROLLBACK: Reverting PROD to Version $(TARGET_VERSION)"
	pkill -f '[h]ost-info.*prod' || true
	mkdir -p /opt/app
	curl -u $(JFROG_USER):$(JFROG_PASS) -s -o /opt/app/host-info.jar \
		$(JFROG_URL)/production_repo/$(GROUP_ID)/$(ARTIFACT_ID)/$(TARGET_VERSION)/$(ARTIFACT_ID)-$(TARGET_VERSION).jar
	nohup java -jar /opt/app/host-info.jar \
		--spring.profiles.active=prod \
		-DSPRING_DATASOURCE_URL=jdbc:postgresql://$(DB_HOST):5432/$(DB_NAME_PROD) \
		-DSPRING_DATASOURCE_USERNAME=$(DB_USER) \
		-DSPRING_DATASOURCE_PASSWORD=$(DB_PASS) \
		-DSERVER_PORT=$(SERVER_PORT) \
		> /var/log/host-info-prod.log 2>&1 &
	@sleep 45
	BASE_URL=http://$(APP_HOST):$(SERVER_PORT) EXPECTED_VERSION=$(TARGET_VERSION) ./smoke/smoke.sh

# ==============================================================================
# Utility
# ==============================================================================
clean:
	mvn clean
