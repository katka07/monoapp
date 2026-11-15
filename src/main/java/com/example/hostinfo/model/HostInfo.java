package com.example.hostinfo.model;

public record HostInfo(
    String hostname,
    String containerId,
    String ipAddress,
    String currentTimeUtc,
    String currentTimeLocal,
    String environment,
    String appVersion,
    String gitSha,
    boolean container,
    boolean kubernetes,
    String instanceId) {
}