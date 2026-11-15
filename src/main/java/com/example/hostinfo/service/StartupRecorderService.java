package com.example.hostinfo.service;

import java.time.Clock;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.example.hostinfo.detector.EnvironmentDetector;
import com.example.hostinfo.model.InstanceStartup;
import com.example.hostinfo.repository.InstanceStartupRepository;

@Service
public class StartupRecorderService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupRecorderService.class);

    private final InstanceStartupRepository repository;
    private final EnvironmentDetector envDetector;
    private final Clock clock;
    private final String appVersion;
    private final String gitSha;
    private final String activeProfile;

    public StartupRecorderService(InstanceStartupRepository repository,
                                  EnvironmentDetector envDetector,
                                  Clock clock,
                                  @Value("${app.version:unknown}") String appVersion,
                                  @Value("${app.git-sha:unknown}") String gitSha,
                                  @Value("${spring.profiles.active:default}") String activeProfile) {
        this.repository = repository;
        this.envDetector = envDetector;
        this.clock = clock;
        this.appVersion = appVersion;
        this.gitSha = gitSha;
        this.activeProfile = activeProfile;
    }

    @Override
    public void run(ApplicationArguments args) {
        InstanceStartup record = new InstanceStartup(
                envDetector.getHostname(),
                appVersion,
                gitSha,
                activeProfile,
                Instant.now(clock)
        );
        repository.save(record);
        log.info("Recorded startup: hostname={}, version={}, env={}",
                record.getHostname(), appVersion, activeProfile);
    }
}