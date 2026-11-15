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
class InfoApiControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsHostInfoJson() throws Exception {
        mockMvc.perform(get("/api/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hostname").isNotEmpty())
                .andExpect(jsonPath("$.currentTimeUtc").isNotEmpty())
                .andExpect(jsonPath("$.environment").value("test"))
                .andExpect(jsonPath("$.appVersion").value("0.0.1-TEST"))
                .andExpect(jsonPath("$.gitSha").value("abc1234"))
                .andExpect(jsonPath("$.instanceId").isNotEmpty());
    }
}
