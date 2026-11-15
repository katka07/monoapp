package com.example.hostinfo.integration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.hostinfo.integration.AbstractIntegrationTest;
import com.example.hostinfo.model.InstanceStartup;
import com.example.hostinfo.repository.InstanceStartupRepository;

@Tag("integration")
class InstanceStartupRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private InstanceStartupRepository repository;

    @Test
    void savesAndRetrievesStartupRecord() {
        InstanceStartup record = new InstanceStartup(
                "repo-test-host", "0.0.1", "abc1234",
                "test", Instant.now());

        InstanceStartup saved = repository.save(record);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getHostname()).isEqualTo("repo-test-host");
    }

    @Test
    void findsTop10ByStartedAtDesc() {
        for (int i = 0; i < 12; i++) {
            repository.save(new InstanceStartup(
                    "host-" + i, "0.0.1", "abc",
                    "test", Instant.now().plusSeconds(i)));
        }

        List<InstanceStartup> top10 = repository.findTop10ByOrderByStartedAtDesc();

        assertThat(top10).hasSize(10);
        assertThat(top10.get(0).getHostname()).isEqualTo("host-11");
    }
}