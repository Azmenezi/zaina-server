package com.zaina.zaina.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaina.zaina.TestUtils
import com.zaina.zaina.dto.LoginRequest
import com.zaina.zaina.dto.RegisterRequest
import com.zaina.zaina.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
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
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should register new user successfully`() {
        val registerRequest = RegisterRequest(
            email = "newuser@example.com",
            password = "password123",
            name = "New User",
            position = "Software Engineer",
            company = "Tech Company",
            bio = "Passionate about technology"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.role").value("APPLICANT"))
            .andExpect(jsonPath("$.name").value("New User"))
    }

    @Test
    fun `should fail registration with duplicate email`() {
        val registerRequest = RegisterRequest(
            email = TestUtils.APPLICANT_EMAIL, // This email already exists
            password = "password123",
            name = "Duplicate User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail registration with invalid email`() {
        val registerRequest = RegisterRequest(
            email = "invalid-email",
            password = "password123",
            name = "Test User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail registration with short password`() {
        val registerRequest = RegisterRequest(
            email = "test@example.com",
            password = "123", // Too short
            name = "Test User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should login with valid credentials`() {
        val loginRequest = LoginRequest(
            email = TestUtils.APPLICANT_EMAIL,
            password = TestUtils.TEST_PASSWORD
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.email").value(TestUtils.APPLICANT_EMAIL))
            .andExpect(jsonPath("$.role").value("APPLICANT"))
    }

    @Test
    fun `should fail login with invalid email`() {
        val loginRequest = LoginRequest(
            email = "nonexistent@example.com",
            password = TestUtils.TEST_PASSWORD
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail login with invalid password`() {
        val loginRequest = LoginRequest(
            email = TestUtils.APPLICANT_EMAIL,
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail login with invalid request format`() {
        val invalidJson = """{"email": "test@example.com"}""" // Missing password

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should login different user roles`() {
        // Test participant login
        val participantLogin = LoginRequest(
            email = TestUtils.PARTICIPANT_EMAIL,
            password = TestUtils.TEST_PASSWORD
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participantLogin))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.role").value("PARTICIPANT"))

        // Test alumna login
        val alumnaLogin = LoginRequest(
            email = TestUtils.ALUMNA_EMAIL,
            password = TestUtils.TEST_PASSWORD
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(alumnaLogin))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.role").value("ALUMNA"))
    }
} 