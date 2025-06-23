package com.zaina.zaina.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zaina.zaina.TestUtils
import com.zaina.zaina.dto.CreateEventRequest
import com.zaina.zaina.dto.RsvpRequest
import com.zaina.zaina.dto.UpdateEventRequest
import com.zaina.zaina.entity.RsvpStatus
import com.zaina.zaina.repository.EventRepository
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
import java.time.LocalDateTime

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var eventRepository: EventRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should get public events without authentication`() {
        mockMvc.perform(get("/api/events/public"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get all events when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/events")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get upcoming events when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        mockMvc.perform(
            get("/api/events/upcoming")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get event by id when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val events = eventRepository.findAll()
        val event = events.first()

        mockMvc.perform(
            get("/api/events/${event.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(event.id.toString()))
            .andExpect(jsonPath("$.title").value(event.title))
    }

    @Test
    fun `participant should create event successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateEventRequest(
            title = "New Test Event",
            description = "Test event description",
            date = LocalDateTime.now().plusDays(10),
            location = "Test Location",
            isPublic = false
        )

        mockMvc.perform(
            post("/api/events")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("New Test Event"))
            .andExpect(jsonPath("$.description").value("Test event description"))
            .andExpect(jsonPath("$.location").value("Test Location"))
            .andExpect(jsonPath("$.isPublic").value(false))
    }

    @Test
    fun `alumna should create event successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.ALUMNA_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateEventRequest(
            title = "Alumni Event",
            description = "Event for alumni",
            date = LocalDateTime.now().plusDays(5),
            location = "Alumni Center",
            isPublic = true
        )

        mockMvc.perform(
            post("/api/events")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Alumni Event"))
            .andExpect(jsonPath("$.isPublic").value(true))
    }

    @Test
    fun `applicant should not be able to create event`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)

        val createRequest = CreateEventRequest(
            title = "Unauthorized Event",
            description = "This should fail",
            date = LocalDateTime.now().plusDays(1),
            isPublic = false
        )

        mockMvc.perform(
            post("/api/events")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should update event successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val events = eventRepository.findAll()
        val event = events.first()

        val updateRequest = UpdateEventRequest(
            title = "Updated Event Title",
            description = "Updated description",
            date = LocalDateTime.now().plusDays(15),
            location = "Updated Location",
            isPublic = true
        )

        mockMvc.perform(
            put("/api/events/${event.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Event Title"))
            .andExpect(jsonPath("$.description").value("Updated description"))
    }

    @Test
    fun `applicant should not be able to update event`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val events = eventRepository.findAll()
        val event = events.first()

        val updateRequest = UpdateEventRequest(
            title = "Hacked Title",
            description = "This should fail",
            date = LocalDateTime.now().plusDays(1),
            isPublic = false
        )

        mockMvc.perform(
            put("/api/events/${event.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `participant should delete event successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val events = eventRepository.findAll()
        val event = events.first()

        mockMvc.perform(
            delete("/api/events/${event.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `applicant should not be able to delete event`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.APPLICANT_EMAIL, TestUtils.TEST_PASSWORD)
        val events = eventRepository.findAll()
        val event = events.first()

        mockMvc.perform(
            delete("/api/events/${event.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should RSVP to event successfully`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val events = eventRepository.findAll()
        val event = events.first()

        val rsvpRequest = RsvpRequest(
            eventId = event.id,
            status = RsvpStatus.GOING
        )

        mockMvc.perform(
            post("/api/events/rsvp")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rsvpRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.eventId").value(event.id.toString()))
            .andExpect(jsonPath("$.rsvpStatus").value("GOING"))
    }

    @Test
    fun `should get event attendees when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val events = eventRepository.findAll()
        val event = events.first()

        mockMvc.perform(
            get("/api/events/${event.id}/attendees")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should get user events when authenticated`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)
        val user = userRepository.findByEmail(TestUtils.PARTICIPANT_EMAIL)!!

        mockMvc.perform(
            get("/api/events/user/${user.id}")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isArray)
    }

    @Test
    fun `should fail to access protected endpoints without authentication`() {
        mockMvc.perform(get("/api/events"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(post("/api/events"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(put("/api/events/123"))
            .andExpect(status().isUnauthorized)

        mockMvc.perform(delete("/api/events/123"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should validate event creation request`() {
        val token = TestUtils.getAuthToken(mockMvc, objectMapper, TestUtils.PARTICIPANT_EMAIL, TestUtils.TEST_PASSWORD)

        // Missing required fields
        val invalidRequest = """{"title": ""}"""

        mockMvc.perform(
            post("/api/events")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest)
        )
            .andExpect(status().isBadRequest)
    }
} 