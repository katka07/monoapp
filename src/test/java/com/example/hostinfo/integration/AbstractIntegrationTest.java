package com.example.hostinfo.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.profiles.active=test")
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {
}
