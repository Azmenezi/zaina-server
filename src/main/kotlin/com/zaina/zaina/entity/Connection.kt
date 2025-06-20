package com.zaina.zaina.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "connections")
data class Connection(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "requester_id", nullable = false)
    val requesterId: UUID,

    @Column(name = "target_id", nullable = false)
    val targetId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ConnectionType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: ConnectionStatus = ConnectionStatus.PENDING,

    @Column(name = "requested_at", nullable = false)
    val requestedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "responded_at")
    val respondedAt: LocalDateTime? = null
)

enum class ConnectionType {
    CONNECT, MENTORSHIP
}

enum class ConnectionStatus {
    PENDING, ACCEPTED, DECLINED
} 