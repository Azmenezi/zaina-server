package com.zaina.zaina.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaina.zaina.TestUtils
import com.zaina.zaina.dto.CreateResourceRequest
import com.zaina.zaina.dto.UpdateResourceRequest
import com.zaina.zaina.entity.ResourceType
import com.zaina.zaina.entity.UserRole
import com.zaina.zaina.repository.ResourceRepository
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
class ResourceControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var resourceRepository: ResourceRepository

    @Test
    fun `applicant should get only accessible resources`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/resources")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
            // Should only get resources available to applicants
    }

    @Test
    fun `participant should get participant and alumni resources`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/resources")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `alumna should get participant and alumni resources`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.ALUMNA_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/resources")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get resource by id when authenticated and authorized`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val resources = resourceRepository.findAll()
        val resource = resources.first { it.targetRoles.contains(UserRole.PARTICIPANT) }

        mockMvc.perform(
            get("/api/resources/${resource.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(resource.id.toString()))
            .andExpect(jsonPath("$.title").value(resource.title))
    }

    @Test
    fun `should fail to get restricted resource`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val resources = resourceRepository.findAll()
        val restrictedResource = resources.first { 
            it.targetRoles.isNotEmpty() && !it.targetRoles.contains(UserRole.APPLICANT) 
        }

        mockMvc.perform(
            get("/api/resources/${restrictedResource.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should get resources by type when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/resources/type/PDF")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get resources by module when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/resources/module/Leadership Development")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should search resources when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/resources/search")
                .param("query", "Leadership")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `participant should create resource successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateResourceRequest(
            title = "New Test Resource",
            description = "Test resource description",
            type = ResourceType.PDF,
            url = "https://example.com/test-resource.pdf",
            targetRoles = listOf(UserRole.PARTICIPANT, UserRole.ALUMNA),
            module = "Test Module"
        )

        mockMvc.perform(
            post("/api/resources")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("New Test Resource"))
            .andExpect(jsonPath("$.description").value("Test resource description"))
            .andExpect(jsonPath("$.type").value("PDF"))
            .andExpect(jsonPath("$.url").value("https://example.com/test-resource.pdf"))
    }

    @Test
    fun `alumna should create resource successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.ALUMNA_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateResourceRequest(
            title = "Alumni Resource",
            description = "Resource for alumni",
            type = ResourceType.VIDEO,
            url = "https://example.com/alumni-video",
            targetRoles = listOf(UserRole.ALUMNA),
            module = "Alumni Exclusive"
        )

        mockMvc.perform(
            post("/api/resources")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Alumni Resource"))
            .andExpect(jsonPath("$.type").value("VIDEO"))
    }

    @Test
    fun `applicant should not be able to create resource`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateResourceRequest(
            title = "Unauthorized Resource",
            description = "This should fail",
            type = ResourceType.LINK,
            url = "https://example.com/unauthorized",
            targetRoles = listOf(UserRole.APPLICANT)
        )

        mockMvc.perform(
            post("/api/resources")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should update resource successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val resources = resourceRepository.findAll()
        val resource = resources.first()

        val updateRequest = UpdateResourceRequest(
            title = "Updated Resource Title",
            description = "Updated description",
            type = ResourceType.LINK,
            url = "https://example.com/updated-resource",
            targetRoles = listOf(UserRole.PARTICIPANT),
            module = "Updated Module"
        )

        mockMvc.perform(
            put("/api/resources/${resource.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Resource Title"))
            .andExpect(jsonPath("$.description").value("Updated description"))
    }

    @Test
    fun `applicant should not be able to update resource`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val resources = resourceRepository.findAll()
        val resource = resources.first()

        val updateRequest = UpdateResourceRequest(
            title = "Hacked Title",
            description = "This should fail",
            type = ResourceType.PDF,
            url = "https://example.com/hacked"
        )

        mockMvc.perform(
            put("/api/resources/${resource.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should delete resource successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val resources = resourceRepository.findAll()
        val resource = resources.first()

        mockMvc.perform(
            delete("/api/resources/${resource.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `applicant should not be able to delete resource`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val resources = resourceRepository.findAll()
        val resource = resources.first()

        mockMvc.perform(
            delete("/api/resources/${resource.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should fail to access protected endpoints without authentication`() {
        mockMvc.perform(get("/api/resources"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(post("/api/resources"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(put("/api/resources/123"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(delete("/api/resources/123"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should validate resource creation request`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        // Missing required fields
        val invalidRequest = """{"title": "", "url": ""}"""

        mockMvc.perform(
            post("/api/resources")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
    }
} 