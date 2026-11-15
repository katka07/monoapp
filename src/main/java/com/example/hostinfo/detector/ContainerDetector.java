package com.example.hostinfo.detector;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

@Component
public class ContainerDetector {

  private final Predicate<Path> fileExists;

  public ContainerDetector() {
    this(Files::exists);
  }

  // Visible for testing
  public ContainerDetector(Predicate<Path> fileExists) {
    this.fileExists = fileExists;
  }

  public boolean isDocker() {
    return fileExists.test(Path.of("/.dockerenv"));
  }

  public String getContainerId() {
    if (!isDocker()) {
      return "n/a";
    }
    Path cgroupPath = Path.of("/proc/self/cgroup");
    if (!fileExists.test(cgroupPath)) {
      return "unknown";
    }
    try {
      return Files.lines(cgroupPath)
          .filter(line -> line.contains("docker") || line.contains("containerd"))
          .map(line -> {
            String[] parts = line.split("/");
            return parts[parts.length - 1];
          })
          .filter(id -> id.length() >= 12)
          .map(id -> id.substring(0, 12))
          .findFirst()
          .orElse("unknown");
    } catch (IOException e) {
      return "unknown";
    }
  }

  public boolean isContainer() {
    return isDocker()
        || fileExists.test(Path.of("/run/.containerenv"))
        || isKubernetesContainer();
  }

  private boolean isKubernetesContainer() {
    return fileExists.test(Path.of("/var/run/secrets/kubernetes.io"));
  }
}
