package com.example.hostinfo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hostinfo.model.HostInfo;
import com.example.hostinfo.service.HostInfoService;

@RestController
@RequestMapping("/api")
public class InfoApiController {

    private final HostInfoService hostInfoService;

    public InfoApiController(HostInfoService hostInfoService) {
        this.hostInfoService = hostInfoService;
    }

    @GetMapping("/info")
    public ResponseEntity<HostInfo> getInfo() {
        return ResponseEntity.ok(hostInfoService.getHostInfo());
    }
}