package com.zaina.zaina.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaina.zaina.TestUtils
import com.zaina.zaina.dto.UpdateProfileRequest
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.repository.UserRepository
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
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should get current user when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/users/me")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(TestUtils.APPLICANT_EMAIL))
            .andExpect(jsonPath("$.role").value("APPLICANT"))
            .andExpect(jsonPath("$.profile").exists())
    }

    @Test
    fun `should fail to get current user without authentication`() {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should get user by id when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val user = userRepository.findByEmail(TestUtils.PARTICIPANT_EMAIL)!!

        mockMvc.perform(
            get("/api/users/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(TestUtils.PARTICIPANT_EMAIL))
            .andExpect(jsonPath("$.role").value("PARTICIPANT"))
    }

    @Test
    fun `should get all users when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/users")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(3)) // Should have 3 test users
    }

    @Test
    fun `should get users by role when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/users/role/PARTICIPANT")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$[0].role").value("PARTICIPANT"))
    }

    @Test
    fun `should get users by cohort when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val user = userRepository.findByEmail(TestUtils.PARTICIPANT_EMAIL)!!

        mockMvc.perform(
            get("/api/users/cohort/${user.cohortId}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get profile by user id when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val user = userRepository.findByEmail(TestUtils.APPLICANT_EMAIL)!!

        mockMvc.perform(
            get("/api/profiles/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").exists())
            .andExpect(jsonPath("$.userId").value(user.id.toString()))
    }

    @Test
    fun `should update own profile when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val user = userRepository.findByEmail(TestUtils.APPLICANT_EMAIL)!!

        val updateRequest = UpdateProfileRequest(
            name = "Updated Name",
            position = "Senior Engineer",
            company = "New Company",
            skills = listOf("Kotlin", "Spring Boot", "Testing"),
            bio = "Updated bio",
            imageUrl = "https://example.com/new-image.jpg"
        )

        mockMvc.perform(
            put("/api/profiles/${user.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Name"))
            .andExpect(jsonPath("$.position").value("Senior Engineer"))
            .andExpect(jsonPath("$.company").value("New Company"))
            .andExpect(jsonPath("$.bio").value("Updated bio"))
    }

    @Test
    fun `should fail to update other user's profile`() {
        val applicantToken = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val participantUser = userRepository.findByEmail(TestUtils.PARTICIPANT_EMAIL)!!

        val updateRequest = UpdateProfileRequest(
            name = "Hacker Name",
            bio = "I'm trying to update someone else's profile"
        )

        mockMvc.perform(
            put("/api/profiles/${participantUser.id}")
                .header("Authorization", "Bearer $applicantToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should search profiles when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/profiles/search")
                .param("query", "Sarah")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should fail to access protected endpoints without token`() {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(get("/api/profiles/123"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should fail with invalid token`() {
        mockMvc.perform(
            get("/api/users/me")
                .header("Authorization", "Bearer invalid-token")
        )
            .andExpect(status().isUnauthorized)
    }
} 