package com.example.hostinfo.service;

import com.example.hostinfo.detector.ContainerDetector;
import com.example.hostinfo.detector.EnvironmentDetector;
import com.example.hostinfo.model.HostInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class HostInfoService {

    private static final DateTimeFormatter UTC_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
    private static final DateTimeFormatter LOCAL_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

    private final EnvironmentDetector envDetector;
    private final ContainerDetector containerDetector;
    private final Clock clock;
    private final String appVersion;
    private final String gitSha;
    private final String instanceId;
    private final String activeProfile;

    public HostInfoService(EnvironmentDetector envDetector,
                           ContainerDetector containerDetector,
                           Clock clock,
                           @Value("${app.version:unknown}") String appVersion,
                           @Value("${app.git-sha:unknown}") String gitSha,
                           String instanceId,
                           @Value("${spring.profiles.active:default}") String activeProfile) {
        this.envDetector = envDetector;
        this.containerDetector = containerDetector;
        this.clock = clock;
        this.appVersion = appVersion;
        this.gitSha = gitSha;
        this.instanceId = instanceId;
        this.activeProfile = activeProfile;
    }

    public HostInfo getHostInfo() {
        ZonedDateTime nowUtc = ZonedDateTime.now(clock.withZone(ZoneId.of("UTC")));
        ZonedDateTime nowLocal = ZonedDateTime.now(clock);

        return new HostInfo(
                envDetector.getHostname(),
                containerDetector.getContainerId(),
                envDetector.getIpAddress(),
                nowUtc.format(UTC_FMT),
                nowLocal.format(LOCAL_FMT),
                activeProfile,
                appVersion,
                gitSha,
                containerDetector.isContainer(),
                envDetector.isKubernetes(),
                instanceId
        );
    }
}