package com.example.hostinfo.detector;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.function.Function;

@Component
public class EnvironmentDetector {

  private final Function<String, String> envLookup;

  public EnvironmentDetector() {
    this(System::getenv);
  }

  // Visible for testing
  public EnvironmentDetector(Function<String, String> envLookup) {
    this.envLookup = envLookup;
  }

  public String getHostname() {
    String envHostname = envLookup.apply("HOSTNAME");
    if (envHostname != null && !envHostname.isBlank()) {
      return envHostname;
    }
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      return "unknown";
    }
  }

  public String getIpAddress() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      return "unknown";
    }
  }

  public boolean isKubernetes() {
    return envLookup.apply("KUBERNETES_SERVICE_HOST") != null;
  }

  public String getPodName() {
    String pod = envLookup.apply("POD_NAME");
    return pod != null ? pod : "n/a";
  }
}
