package com.zaina.zaina.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaina.zaina.TestUtils
import com.zaina.zaina.dto.CreateCohortRequest
import com.zaina.zaina.dto.UpdateCohortRequest
import com.zaina.zaina.repository.CohortRepository
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
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CohortControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var cohortRepository: CohortRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should get all cohorts when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/cohorts")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$.length()").value(2)) // Should have 2 test cohorts
    }

    @Test
    fun `should get cohort by id when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()

        mockMvc.perform(
            get("/api/cohorts/${cohort.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(cohort.id.toString()))
            .andExpect(jsonPath("$.name").value(cohort.name))
    }

    @Test
    fun `should get current cohorts when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/cohorts/current")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get cohort members when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()

        mockMvc.perform(
            get("/api/cohorts/${cohort.id}/members")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `participant should create cohort successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateCohortRequest(
            name = "RISE 2026",
            startDate = LocalDate.of(2026, 1, 15),
            endDate = LocalDate.of(2026, 12, 15)
        )

        mockMvc.perform(
            post("/api/cohorts")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("RISE 2026"))
            .andExpect(jsonPath("$.startDate").value("2026-01-15"))
            .andExpect(jsonPath("$.endDate").value("2026-12-15"))
    }

    @Test
    fun `alumna should create cohort successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.ALUMNA_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateCohortRequest(
            name = "RISE Alumni 2027",
            startDate = LocalDate.of(2027, 1, 15),
            endDate = LocalDate.of(2027, 12, 15)
        )

        mockMvc.perform(
            post("/api/cohorts")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("RISE Alumni 2027"))
    }

    @Test
    fun `applicant should not be able to create cohort`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateCohortRequest(
            name = "Unauthorized Cohort",
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 12, 31)
        )

        mockMvc.perform(
            post("/api/cohorts")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should update cohort successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()

        val updateRequest = UpdateCohortRequest(
            name = "Updated Cohort Name",
            startDate = LocalDate.of(2025, 2, 1),
            endDate = LocalDate.of(2025, 11, 30)
        )

        mockMvc.perform(
            put("/api/cohorts/${cohort.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Updated Cohort Name"))
            .andExpect(jsonPath("$.startDate").value("2025-02-01"))
            .andExpect(jsonPath("$.endDate").value("2025-11-30"))
    }

    @Test
    fun `applicant should not be able to update cohort`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()

        val updateRequest = UpdateCohortRequest(
            name = "Hacked Cohort",
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 12, 31)
        )

        mockMvc.perform(
            put("/api/cohorts/${cohort.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should delete cohort successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()

        mockMvc.perform(
            delete("/api/cohorts/${cohort.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `applicant should not be able to delete cohort`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()

        mockMvc.perform(
            delete("/api/cohorts/${cohort.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should assign user to cohort successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()
        val user = userRepository.findByEmail(TestUtils.APPLICANT_EMAIL)!!

        mockMvc.perform(
            post("/api/cohorts/${cohort.id}/members/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `applicant should not be able to assign user to cohort`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()
        val user = userRepository.findByEmail(TestUtils.PARTICIPANT_EMAIL)!!

        mockMvc.perform(
            post("/api/cohorts/${cohort.id}/members/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should remove user from cohort successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()
        val user = userRepository.findByEmail(TestUtils.ALUMNA_EMAIL)!!

        mockMvc.perform(
            delete("/api/cohorts/${cohort.id}/members/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `applicant should not be able to remove user from cohort`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val cohorts = cohortRepository.findAll()
        val cohort = cohorts.first()
        val user = userRepository.findByEmail(TestUtils.PARTICIPANT_EMAIL)!!

        mockMvc.perform(
            delete("/api/cohorts/${cohort.id}/members/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail to access protected endpoints without authentication`() {
        mockMvc.perform(get("/api/cohorts"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(post("/api/cohorts"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(put("/api/cohorts/123"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(delete("/api/cohorts/123"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should validate cohort creation request`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        // Missing required fields
        val invalidRequest = """{"name": ""}"""

        mockMvc.perform(
            post("/api/cohorts")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail with invalid cohort id`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/cohorts/invalid-uuid")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }
} 