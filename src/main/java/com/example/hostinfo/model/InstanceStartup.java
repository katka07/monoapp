package com.example.hostinfo.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "instance_startup")
public class InstanceStartup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String hostname;

  @Column(name = "app_version", nullable = false, length = 100)
  private String appVersion;

  @Column(name = "git_sha", length = 100)
  private String gitSha;

  @Column(nullable = false, length = 50)
  private String environment;

  @Column(name = "started_at", nullable = false)
  private Instant startedAt;

  public InstanceStartup() {
  }

  public InstanceStartup(String hostname, String appVersion,
      String gitSha, String environment, Instant startedAt) {
    this.hostname = hostname;
    this.appVersion = appVersion;
    this.gitSha = gitSha;
    this.environment = environment;
    this.startedAt = startedAt;
  }

  public Long getId() {
    return id;
  }

  public String getHostname() {
    return hostname;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public String getGitSha() {
    return gitSha;
  }

  public String getEnvironment() {
    return environment;
  }

  public Instant getStartedAt() {
    return startedAt;
  }
}