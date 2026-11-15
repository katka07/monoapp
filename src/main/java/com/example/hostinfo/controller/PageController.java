package com.example.hostinfo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.hostinfo.model.HostInfo;
import com.example.hostinfo.service.HostInfoService;

@Controller
public class PageController {

    private final HostInfoService hostInfoService;

    public PageController(HostInfoService hostInfoService) {
        this.hostInfoService = hostInfoService;
    }

    @GetMapping("/")
    public String index(Model model) {
        HostInfo info = hostInfoService.getHostInfo();
        model.addAttribute("info", info);
        return "index";
    }
}