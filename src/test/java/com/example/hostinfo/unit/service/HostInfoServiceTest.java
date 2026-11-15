package com.example.hostinfo.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.example.hostinfo.detector.ContainerDetector;
import com.example.hostinfo.detector.EnvironmentDetector;
import com.example.hostinfo.model.HostInfo;
import com.example.hostinfo.service.HostInfoService;

@Tag("unit")
class HostInfoServiceTest {

    @Test
    void returnsAllFields() {
        var env = mock(EnvironmentDetector.class);
        when(env.getHostname()).thenReturn("test-host");
        when(env.getIpAddress()).thenReturn("10.0.0.5");
        when(env.isKubernetes()).thenReturn(false);

        var container = mock(ContainerDetector.class);
        when(container.getContainerId()).thenReturn("n/a");
        when(container.isContainer()).thenReturn(false);

        Clock fixed = Clock.fixed(
                Instant.parse("2025-01-15T12:00:00Z"), ZoneId.of("UTC"));

        var service = new HostInfoService(
                env, container, fixed,
                "1.2.3", "abc1234", "inst-1", "dev");

        HostInfo info = service.getHostInfo();

        assertThat(info.hostname()).isEqualTo("test-host");
        assertThat(info.ipAddress()).isEqualTo("10.0.0.5");
        assertThat(info.containerId()).isEqualTo("n/a");
        assertThat(info.container()).isFalse();
        assertThat(info.kubernetes()).isFalse();
        assertThat(info.environment()).isEqualTo("dev");
        assertThat(info.appVersion()).isEqualTo("1.2.3");
        assertThat(info.gitSha()).isEqualTo("abc1234");
        assertThat(info.instanceId()).isEqualTo("inst-1");
        assertThat(info.currentTimeUtc()).contains("2025-01-15");
    }

    @Test
    void detectsContainerAndK8s() {
        var env = mock(EnvironmentDetector.class);
        when(env.getHostname()).thenReturn("pod-1");
        when(env.getIpAddress()).thenReturn("172.17.0.2");
        when(env.isKubernetes()).thenReturn(true);

        var container = mock(ContainerDetector.class);
        when(container.getContainerId()).thenReturn("a1b2c3d4e5f6");
        when(container.isContainer()).thenReturn(true);

        Clock fixed = Clock.fixed(
                Instant.parse("2025-06-01T00:00:00Z"), ZoneId.of("UTC"));

        var service = new HostInfoService(
                env, container, fixed,
                "2.0.0", "def5678", "inst-2", "prod");

        HostInfo info = service.getHostInfo();

        assertThat(info.container()).isTrue();
        assertThat(info.kubernetes()).isTrue();
        assertThat(info.containerId()).isEqualTo("a1b2c3d4e5f6");
    }
}
