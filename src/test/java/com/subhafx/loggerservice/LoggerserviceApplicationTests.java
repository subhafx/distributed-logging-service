package com.subhafx.loggerservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.subhafx.loggerservice.dto.LogDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@SpringBootTest
@AutoConfigureMockMvc
class LoggerserviceApplicationTests {
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    LoggerserviceApplicationTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @DisplayName("Log API")
    void shouldSaveLogs() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/api/log")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getSampleLog()))
        ).andExpect(MockMvcResultMatchers.status().isCreated());
    }

    private LogDTO getSampleLog(){
        SecureRandom random = new SecureRandom();
        String[] events = new String[] {
                "login",
                "logout",
                "guest",
                "registration"
        };
        return new LogDTO(
                random.nextLong(100000),
                System.currentTimeMillis() / 1000L,
                random.nextLong(1000000000),
                events[random.nextInt(events.length)]
        );
    }

}
