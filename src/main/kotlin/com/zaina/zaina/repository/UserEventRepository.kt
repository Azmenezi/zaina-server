package com.zaina.zaina.repository

import com.zaina.zaina.entity.UserEvent
import com.zaina.zaina.entity.RsvpStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserEventRepository : JpaRepository<UserEvent, UUID> {
    fun findByUserId(userId: UUID): List<UserEvent>
    fun findByEventId(eventId: UUID): List<UserEvent>
    fun findByUserIdAndEventId(userId: UUID, eventId: UUID): UserEvent?
    fun findByRsvpStatus(rsvpStatus: RsvpStatus): List<UserEvent>
    
    @Query("SELECT ue FROM UserEvent ue WHERE ue.user.id = :userId AND ue.rsvpStatus = :status")
    fun findByUserIdAndRsvpStatus(userId: UUID, status: RsvpStatus): List<UserEvent>
    
    @Query("SELECT ue FROM UserEvent ue WHERE ue.event.id = :eventId AND ue.rsvpStatus = :status")
    fun findByEventIdAndRsvpStatus(eventId: UUID, status: RsvpStatus): List<UserEvent>
    
    fun existsByUserIdAndEventId(userId: UUID, eventId: UUID): Boolean
} 