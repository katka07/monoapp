package com.example.hostinfo.unit.detector;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.example.hostinfo.detector.ContainerDetector;

@Tag("unit")
class ContainerDetectorTest {

    private ContainerDetector withFiles(String... paths) {
        Set<Path> existing = Set.of(paths).stream()
                .map(Path::of)
                .collect(java.util.stream.Collectors.toSet());
        return new ContainerDetector(existing::contains);
    }

    @Test
    void isDockerWhenDockerenvExists() {
        var detector = withFiles("/.dockerenv");
        assertThat(detector.isDocker()).isTrue();
    }

    @Test
    void isNotDockerWhenNoDockerenv() {
        var detector = withFiles();
        assertThat(detector.isDocker()).isFalse();
    }

    @Test
    void isContainerTrueForDocker() {
        var detector = withFiles("/.dockerenv");
        assertThat(detector.isContainer()).isTrue();
    }

    @Test
    void isContainerTrueForPodman() {
        var detector = withFiles("/run/.containerenv");
        assertThat(detector.isContainer()).isTrue();
    }

    @Test
    void isContainerFalseOnBareHost() {
        var detector = withFiles();
        assertThat(detector.isContainer()).isFalse();
    }

    @Test
    void containerIdNAWhenNotDocker() {
        var detector = withFiles();
        assertThat(detector.getContainerId()).isEqualTo("n/a");
    }
}
