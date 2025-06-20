package com.zaina.zaina.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "events")
data class Event(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val title: String,

    @Column(columnDefinition = "TEXT")
    val description: String?,

    @Column(nullable = false)
    val date: LocalDateTime,

    @Column
    val location: String?,

    @Column(name = "is_public", nullable = false)
    val isPublic: Boolean = false,

    @OneToMany(mappedBy = "event", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val userEvents: List<UserEvent> = listOf()
) 