package com.example.hostinfo.integration.config;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.example.hostinfo.HostInfoApplication;

@Tag("integration")
class ProdConfigValidationTest {

    @Test
    void prodProfileFailsWithoutDatasourceUrl() {
        assertThatThrownBy(() ->
                new SpringApplicationBuilder(HostInfoApplication.class)
                        .profiles("prod")
                        .properties(
                                "spring.main.web-application-type=none",
                                "spring.flyway.enabled=false"
                        )
                        .run()
        ).hasRootCauseInstanceOf(Exception.class);
    }
}