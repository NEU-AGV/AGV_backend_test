package com.moxin.agvbackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RestTemplateConfigTest {

    @Autowired
    private RestTemplateConfig restTemplateConfig;

    @Test
    public void testRestTemplate() {
        RestTemplate restTemplate = restTemplateConfig.restTemplate();
        assertNotNull(restTemplate);
    }
}