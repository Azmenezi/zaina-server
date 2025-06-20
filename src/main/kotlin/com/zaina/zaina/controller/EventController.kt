package com.zaina.zaina.controller

import com.zaina.zaina.dto.*
import com.zaina.zaina.service.EventService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = ["*"], maxAge = 3600)
class EventController(
    private val eventService: EventService
) {

    @GetMapping
    fun getAllEvents(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<EventDto>> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val events = eventService.getAllEvents(pageable)
            ResponseEntity.ok(events)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/upcoming")
    fun getUpcomingEvents(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<EventDto>> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val events = eventService.getUpcomingEvents(pageable)
            ResponseEntity.ok(events)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/public")
    fun getPublicEvents(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<EventDto>> {
        return try {
            val pageable: Pageable = PageRequest.of(page, size)
            val events = eventService.getPublicEvents(pageable)
            ResponseEntity.ok(events)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{eventId}")
    fun getEventById(@PathVariable eventId: UUID): ResponseEntity<EventDto> {
        return try {
            val event = eventService.getEventById(eventId)
            ResponseEntity.ok(event)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createEvent(@Valid @RequestBody createRequest: CreateEventRequest): ResponseEntity<EventDto> {
        return try {
            val event = eventService.createEvent(createRequest)
            ResponseEntity.ok(event)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @PathVariable eventId: UUID,
        @Valid @RequestBody updateRequest: UpdateEventRequest
    ): ResponseEntity<EventDto> {
        return try {
            val event = eventService.updateEvent(eventId, updateRequest)
            ResponseEntity.ok(event)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(@PathVariable eventId: UUID): ResponseEntity<Void> {
        return try {
            eventService.deleteEvent(eventId)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @PostMapping("/rsvp")
    fun rsvpToEvent(@Valid @RequestBody rsvpRequest: RsvpRequest): ResponseEntity<EventRsvpDto> {
        return try {
            val rsvp = eventService.rsvpToEvent(rsvpRequest)
            ResponseEntity.ok(rsvp)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{eventId}/attendees")
    fun getEventAttendees(@PathVariable eventId: UUID): ResponseEntity<List<EventRsvpDto>> {
        return try {
            val attendees = eventService.getEventAttendees(eventId)
            ResponseEntity.ok(attendees)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/user/{userId}")
    fun getUserEvents(@PathVariable userId: UUID): ResponseEntity<List<EventDto>> {
        return try {
            val events = eventService.getUserEvents(userId)
            ResponseEntity.ok(events)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
} 