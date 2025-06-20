package com.zaina.zaina.mapper

import com.zaina.zaina.dto.EventDto
import com.zaina.zaina.entity.Event
import com.zaina.zaina.entity.RsvpStatus

object EventMapper {
    
    fun toEventDto(event: Event, rsvpStatus: RsvpStatus? = null, attendeeCount: Int = 0): EventDto {
        return EventDto(
            id = event.id,
            title = event.title,
            description = event.description,
            date = event.date,
            location = event.location,
            isPublic = event.isPublic,
            rsvpStatus = rsvpStatus,
            attendeeCount = attendeeCount
        )
    }
} 