package com.example.hostinfo.config;

import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.UUID;

@Configuration
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public String instanceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @Bean
    public String appVersion(BuildProperties buildProperties) {
        return buildProperties.getVersion();
    }

    @Bean
    public String gitSha(GitProperties gitProperties) {
        return gitProperties.getShortCommitId();
    }
}