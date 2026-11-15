package com.example.hostinfo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {

    private final String appVersion;
    private final String gitSha;
    private final String activeProfile;

    public VersionController(
            @Value("${app.version:unknown}") String appVersion,
            @Value("${app.git-sha:unknown}") String gitSha,
            @Value("${spring.profiles.active:default}") String activeProfile) {
        this.appVersion = appVersion;
        this.gitSha = gitSha;
        this.activeProfile = activeProfile;
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> version() {
        return ResponseEntity.ok(Map.of(
                "version", appVersion,
                "gitSha", gitSha,
                "environment", activeProfile
        ));
    }
}