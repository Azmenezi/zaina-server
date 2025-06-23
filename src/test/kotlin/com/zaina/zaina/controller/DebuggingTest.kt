package com.zaina.zaina.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaina.zaina.dto.LoginRequest
import com.zaina.zaina.dto.RegisterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class DebuggingTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `debug registration failure`() {
        val registerRequest = RegisterRequest(
            email = "debug@example.com",
            password = "password123",
            name = "Debug User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andDo(print()) // This will print the full request/response
    }

    @Test
    fun `debug login failure`() {
        val loginRequest = LoginRequest(
            email = "applicant@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andDo(print()) // This will print the full request/response
    }

    @Test
    fun `check what users exist in database`() {
        // This test will help us see if the mock data is actually loaded
        mockMvc.perform(get("/api/events/public"))
            .andDo(print()) // See if this at least works
    }
} 