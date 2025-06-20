package com.zaina.zaina.repository

import com.zaina.zaina.entity.Event
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface EventRepository : JpaRepository<Event, UUID> {
    fun findByIsPublic(isPublic: Boolean): List<Event>
    fun findByIsPublic(isPublic: Boolean, pageable: Pageable): Page<Event>
    
    @Query("SELECT e FROM Event e WHERE e.date >= :startDate AND e.date <= :endDate")
    fun findByDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Event>
    
    @Query("SELECT e FROM Event e WHERE e.date >= :date ORDER BY e.date ASC")
    fun findUpcomingEvents(date: LocalDateTime = LocalDateTime.now()): List<Event>
    
    @Query("SELECT e FROM Event e WHERE e.date >= :date ORDER BY e.date ASC")
    fun findUpcomingEvents(date: LocalDateTime = LocalDateTime.now(), pageable: Pageable): Page<Event>
    
    @Query("SELECT e FROM Event e WHERE e.date < :date ORDER BY e.date DESC")
    fun findPastEvents(date: LocalDateTime = LocalDateTime.now()): List<Event>
    
    @Query("SELECT e FROM Event e WHERE e.isPublic = true AND e.date >= :date ORDER BY e.date ASC")
    fun findUpcomingPublicEvents(date: LocalDateTime = LocalDateTime.now()): List<Event>
    
    @Query("SELECT e FROM Event e WHERE e.isPublic = true AND e.date >= :date ORDER BY e.date ASC")
    fun findUpcomingPublicEvents(date: LocalDateTime = LocalDateTime.now(), pageable: Pageable): Page<Event>
} 