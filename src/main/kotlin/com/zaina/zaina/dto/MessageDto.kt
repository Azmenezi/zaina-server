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

// WebSocket DTOs for real-time messaging
data class WebSocketMessage(
    val type: MessageType,
    val data: Any
)

data class ChatMessage(
    val id: UUID,
    val senderId: UUID,
    val receiverId: UUID,
    val content: String,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val sentAt: LocalDateTime,
    val senderName: String?
)

data class UserStatus(
    val userId: UUID,
    val online: Boolean,
    val lastSeen: LocalDateTime?
)

data class TypingIndicator(
    val senderId: UUID,
    val receiverId: UUID,
    val typing: Boolean,
    val senderName: String?
)

data class MessageReadReceipt(
    val messageId: UUID,
    val readBy: UUID,
    val readAt: LocalDateTime
)

enum class MessageType {
    CHAT_MESSAGE,
    USER_STATUS,
    TYPING_INDICATOR,
    MESSAGE_READ_RECEIPT,
    CONNECTION_REQUEST,
    CONNECTION_ACCEPTED
} 