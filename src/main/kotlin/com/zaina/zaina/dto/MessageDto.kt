package com.zaina.zaina.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime
import java.util.*

data class SendMessageRequest(
    @field:NotNull(message = "Receiver ID is required")
    val receiverId: UUID,
    
    @field:NotBlank(message = "Content cannot be blank")
    val content: String
)

data class MessageResponse(
    val id: UUID,
    val senderId: UUID,
    val receiverId: UUID,
    val content: String,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val sentAt: LocalDateTime,
    val isRead: Boolean
)

data class ConversationResponse(
    val messages: List<MessageResponse>,
    val otherUserId: UUID,
    val otherUserName: String?
) 