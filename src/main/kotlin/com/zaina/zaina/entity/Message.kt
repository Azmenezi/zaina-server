package com.zaina.zaina.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "messages")
data class Message(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(name = "sender_id", nullable = false)
    val senderId: UUID,

    @Column(name = "receiver_id", nullable = false)
    val receiverId: UUID,

    @Column(columnDefinition = "TEXT", nullable = false)
    val content: String,

    @Column(name = "sent_at", nullable = false)
    val sentAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_read", nullable = false)
    val isRead: Boolean = false
) 