package com.zaina.zaina.mapper

import com.zaina.zaina.dto.MessageResponse
import com.zaina.zaina.entity.Message

object MessageMapper {
    
    fun toMessageResponse(message: Message): MessageResponse {
        return MessageResponse(
            id = message.id,
            senderId = message.senderId,
            receiverId = message.receiverId,
            content = message.content,
            sentAt = message.sentAt,
            isRead = message.isRead
        )
    }
} 