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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SimpleIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should access public events endpoint without authentication`() {
        mockMvc.perform(get("/api/events/public"))
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
    }

    @Test
    fun `should return 401 for protected endpoints without authentication`() {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should access swagger documentation without authentication`() {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should be able to register a new user with proper DTO`() {
        val registerRequest = RegisterRequest(
            email = "newtest@example.com",
            password = "password123",
            name = "New Test User",
            position = "Software Engineer",
            company = "Test Company",
            bio = "Test bio"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email").value("newtest@example.com"))
            .andExpect(jsonPath("$.role").value("APPLICANT"))
    }

    @Test
    fun `should be able to login with existing test user`() {
        val loginRequest = LoginRequest(
            email = "applicant@example.com",
            password = "password123"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email").value("applicant@example.com"))
            .andExpect(jsonPath("$.role").value("APPLICANT"))
    }

    @Test
    fun `should be able to access protected endpoint with valid token`() {
        // First register a user to get a token
        val registerRequest = RegisterRequest(
            email = "authtest@example.com",
            password = "password123",
            name = "Auth Test User"
        )

        val registerResult = mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        val registerResponse = objectMapper.readTree(registerResult.response.contentAsString)
        val token = registerResponse.get("token").asText()

        // Now use the token to access a protected endpoint
        mockMvc.perform(
            get("/api/users/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value("authtest@example.com"))
            .andExpect(jsonPath("$.role").value("APPLICANT"))
    }
} 