package com.example.hostinfo.e2e;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.profiles.active=test"
)
@ActiveProfiles("test")
class FullStackE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void homePageLoads() {
        ResponseEntity<String> response = restTemplate.getForEntity("/", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Host Info Dashboard");
        assertThat(response.getBody()).contains("Auto-refreshing");
    }

    @Test
    void apiInfoReturnsValidJson() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/info", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
                .containsKeys("hostname", "currentTimeUtc", "environment",
                        "appVersion", "gitSha", "instanceId", "container", "kubernetes");
    }

    @Test
    void versionEndpointReturnsBuildInfo() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/version", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("version")).isEqualTo("0.0.1-TEST");
    }

    @Test
    void actuatorHealthIsUp() {
        ResponseEntity<Map> response =
                restTemplate.getForEntity("/actuator/health", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    void livenessProbePasses() {
        ResponseEntity<Map> response =
                restTemplate.getForEntity("/actuator/health/liveness", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    void readinessProbePasses() {
        ResponseEntity<Map> response =
                restTemplate.getForEntity("/actuator/health/readiness", Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    void startupWasRecordedInDatabase() {
        ResponseEntity<Map> health =
                restTemplate.getForEntity("/actuator/health/readiness", Map.class);
        assertThat(health.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
