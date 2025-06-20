package com.zaina.zaina.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "user_events")
data class UserEvent(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore
    val event: Event,

    @Enumerated(EnumType.STRING)
    @Column(name = "rsvp_status", nullable = false)
    val rsvpStatus: RsvpStatus
)

enum class RsvpStatus {
    GOING, NOT_GOING, INTERESTED
} 