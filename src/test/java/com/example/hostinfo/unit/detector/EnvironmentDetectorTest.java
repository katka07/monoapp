package com.example.hostinfo.unit.detector;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.example.hostinfo.detector.EnvironmentDetector;

@Tag("unit")
class EnvironmentDetectorTest {

    @Test
    void hostnameFromEnvVar() {
        var detector = new EnvironmentDetector(key ->
                key.equals("HOSTNAME") ? "web-01" : null);
        assertThat(detector.getHostname()).isEqualTo("web-01");
    }

    @Test
    void hostnameFallsBackWhenEnvBlank() {
        var detector = new EnvironmentDetector(key ->
                key.equals("HOSTNAME") ? "  " : null);
        assertThat(detector.getHostname()).isNotEqualTo("  ");
        assertThat(detector.getHostname()).isNotBlank();
    }

    @Test
    void isKubernetesTrueWhenServiceHostSet() {
        var detector = new EnvironmentDetector(key ->
                key.equals("KUBERNETES_SERVICE_HOST") ? "10.0.0.1" : null);
        assertThat(detector.isKubernetes()).isTrue();
    }

    @Test
    void isKubernetesFalseWhenMissing() {
        var detector = new EnvironmentDetector(key -> null);
        assertThat(detector.isKubernetes()).isFalse();
    }

    @Test
    void podNameReturnsValue() {
        var detector = new EnvironmentDetector(key ->
                key.equals("POD_NAME") ? "host-info-7b8f9" : null);
        assertThat(detector.getPodName()).isEqualTo("host-info-7b8f9");
    }

    @Test
    void podNameDefaultsToNA() {
        var detector = new EnvironmentDetector(key -> null);
        assertThat(detector.getPodName()).isEqualTo("n/a");
    }
}
