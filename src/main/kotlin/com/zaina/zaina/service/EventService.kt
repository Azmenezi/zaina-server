package com.zaina.zaina.service

import com.zaina.zaina.dto.*
import com.zaina.zaina.entity.*
import com.zaina.zaina.mapper.EventMapper
import com.zaina.zaina.repository.EventRepository
import com.zaina.zaina.repository.ProfileRepository
import com.zaina.zaina.repository.UserEventRepository
import com.zaina.zaina.repository.UserRepository
import com.zaina.zaina.security.UserPrincipal
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
class EventService(
    private val eventRepository: EventRepository,
    private val userEventRepository: UserEventRepository,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {

    fun getAllEvents(): List<EventDto> {
        val currentUser = getCurrentUserPrincipal()
        return eventRepository.findAll().map { event ->
            val userEvent = userEventRepository.findByUserIdAndEventId(currentUser.id, event.id)
            val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(event.id, RsvpStatus.GOING).size
            EventMapper.toEventDto(event, userEvent?.rsvpStatus, attendeeCount)
        }
    }

    fun getAllEvents(pageable: Pageable): List<EventDto> {
        val currentUser = getCurrentUserPrincipal()
        return eventRepository.findAll(pageable).content.map { event ->
            val userEvent = userEventRepository.findByUserIdAndEventId(currentUser.id, event.id)
            val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(event.id, RsvpStatus.GOING).size
            EventMapper.toEventDto(event, userEvent?.rsvpStatus, attendeeCount)
        }
    }

    fun getUpcomingEvents(): List<EventDto> {
        val currentUser = getCurrentUserPrincipal()
        return eventRepository.findUpcomingEvents().map { event ->
            val userEvent = userEventRepository.findByUserIdAndEventId(currentUser.id, event.id)
            val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(event.id, RsvpStatus.GOING).size
            EventMapper.toEventDto(event, userEvent?.rsvpStatus, attendeeCount)
        }
    }

    fun getUpcomingEvents(pageable: Pageable): List<EventDto> {
        val currentUser = getCurrentUserPrincipal()
        return eventRepository.findUpcomingEvents(pageable = pageable).content.map { event ->
            val userEvent = userEventRepository.findByUserIdAndEventId(currentUser.id, event.id)
            val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(event.id, RsvpStatus.GOING).size
            EventMapper.toEventDto(event, userEvent?.rsvpStatus, attendeeCount)
        }
    }

    fun getPublicEvents(): List<EventDto> {
        return eventRepository.findUpcomingPublicEvents().map { event ->
            val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(event.id, RsvpStatus.GOING).size
            EventMapper.toEventDto(event, null, attendeeCount)
        }
    }

    fun getPublicEvents(pageable: Pageable): List<EventDto> {
        return eventRepository.findUpcomingPublicEvents(pageable = pageable).content.map { event ->
            val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(event.id, RsvpStatus.GOING).size
            EventMapper.toEventDto(event, null, attendeeCount)
        }
    }

    fun getEventById(eventId: UUID): EventDto {
        val event = eventRepository.findById(eventId)
            .orElseThrow { RuntimeException("Event not found") }
        
        val currentUser = getCurrentUserPrincipal()
        val userEvent = userEventRepository.findByUserIdAndEventId(currentUser.id, event.id)
        val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(event.id, RsvpStatus.GOING).size
        
        return EventMapper.toEventDto(event, userEvent?.rsvpStatus, attendeeCount)
    }

    fun createEvent(createRequest: CreateEventRequest): EventDto {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants, alumni, and mentors can create events (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants, alumni, and mentors can create events")
        }

        val event = Event(
            title = createRequest.title,
            description = createRequest.description,
            date = createRequest.date,
            location = createRequest.location,
            isPublic = createRequest.isPublic
        )

        val savedEvent = eventRepository.save(event)
        return EventMapper.toEventDto(savedEvent)
    }

    fun updateEvent(eventId: UUID, updateRequest: UpdateEventRequest): EventDto {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants, alumni, and mentors can update events (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants, alumni, and mentors can update events")
        }

        val event = eventRepository.findById(eventId)
            .orElseThrow { RuntimeException("Event not found") }

        val updatedEvent = Event(
            id = event.id,
            title = updateRequest.title,
            description = updateRequest.description,
            date = updateRequest.date,
            location = updateRequest.location,
            isPublic = updateRequest.isPublic
        )

        val savedEvent = eventRepository.save(updatedEvent)
        return EventMapper.toEventDto(savedEvent)
    }

    fun deleteEvent(eventId: UUID) {
        val currentUser = getCurrentUserPrincipal()
        
        // Only participants, alumni, and mentors can delete events (business rule)
        if (currentUser.role == UserRole.APPLICANT) {
            throw RuntimeException("Access denied: Only participants, alumni, and mentors can delete events")
        }

        eventRepository.deleteById(eventId)
    }

    fun rsvpToEvent(rsvpRequest: RsvpRequest): EventRsvpDto {
        val currentUser = getCurrentUserPrincipal()
        val event = eventRepository.findById(rsvpRequest.eventId)
            .orElseThrow { RuntimeException("Event not found") }

        val existingUserEvent = userEventRepository.findByUserIdAndEventId(currentUser.id, rsvpRequest.eventId)

        val userEvent = if (existingUserEvent != null) {
            UserEvent(
                id = existingUserEvent.id,
                user = existingUserEvent.user,
                event = existingUserEvent.event,
                rsvpStatus = rsvpRequest.status
            )
        } else {
            val user = userRepository.findById(currentUser.id)
                .orElseThrow { RuntimeException("User not found") }
            UserEvent(
                user = user,
                event = event,
                rsvpStatus = rsvpRequest.status
            )
        }

        val savedUserEvent = userEventRepository.save(userEvent)
        
        return EventRsvpDto(
            eventId = savedUserEvent.event.id,
            userId = savedUserEvent.user.id,
            userName = currentUser.name,
            rsvpStatus = savedUserEvent.rsvpStatus
        )
    }

    fun getEventAttendees(eventId: UUID): List<EventRsvpDto> {
        val userEvents = userEventRepository.findByEventId(eventId)
        return userEvents.map { userEvent ->
            val profile = profileRepository.findByUserId(userEvent.user.id)
            EventRsvpDto(
                eventId = userEvent.event.id,
                userId = userEvent.user.id,
                userName = profile?.name,
                rsvpStatus = userEvent.rsvpStatus
            )
        }
    }

    fun getUserEvents(userId: UUID): List<EventDto> {
        val userEvents = userEventRepository.findByUserId(userId)
        return userEvents.map { userEvent ->
            val attendeeCount = userEventRepository.findByEventIdAndRsvpStatus(userEvent.event.id, RsvpStatus.GOING).size
            EventMapper.toEventDto(userEvent.event, userEvent.rsvpStatus, attendeeCount)
        }
    }

    private fun getCurrentUserPrincipal(): UserPrincipal {
        return SecurityContextHolder.getContext().authentication.principal as UserPrincipal
    }
} 