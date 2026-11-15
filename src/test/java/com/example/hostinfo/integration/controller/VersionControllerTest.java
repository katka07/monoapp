package com.example.hostinfo.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hostinfo.integration.AbstractIntegrationTest;

@Tag("integration")
@AutoConfigureMockMvc
class VersionControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsVersionInfo() throws Exception {
        mockMvc.perform(get("/version"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("0.0.1-TEST"))
                .andExpect(jsonPath("$.gitSha").value("abc1234"))
                .andExpect(jsonPath("$.environment").value("test"));
    }
}