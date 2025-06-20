package com.zaina.zaina.dto

import com.zaina.zaina.entity.RsvpStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class EventDto(
    val id: UUID,
    val title: String,
    val description: String?,
    val date: LocalDateTime,
    val location: String?,
    val isPublic: Boolean,
    val rsvpStatus: RsvpStatus? = null, // Current user's RSVP status
    val attendeeCount: Int = 0
)

data class CreateEventRequest(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    @field:NotNull
    val date: LocalDateTime,
    val location: String? = null,
    val isPublic: Boolean = false
)

data class UpdateEventRequest(
    @field:NotBlank
    val title: String,
    val description: String? = null,
    @field:NotNull
    val date: LocalDateTime,
    val location: String? = null,
    val isPublic: Boolean = false
)

data class RsvpRequest(
    @field:NotNull
    val eventId: UUID,
    @field:NotNull
    val status: RsvpStatus
)

data class EventRsvpDto(
    val eventId: UUID,
    val userId: UUID,
    val userName: String?,
    val rsvpStatus: RsvpStatus
) 