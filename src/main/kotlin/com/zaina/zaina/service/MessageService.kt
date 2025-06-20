package com.zaina.zaina.service

import com.zaina.zaina.dto.*
import com.zaina.zaina.entity.Message
import com.zaina.zaina.mapper.MessageMapper
import com.zaina.zaina.repository.MessageRepository
import com.zaina.zaina.repository.ProfileRepository
import com.zaina.zaina.repository.UserRepository
import com.zaina.zaina.security.UserPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional
class MessageService(
    private val messageRepository: MessageRepository,
    private val userRepository: UserRepository,
    private val profileRepository: ProfileRepository
) {

    fun sendMessage(request: SendMessageRequest): MessageResponse {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val senderId = userPrincipal.id

        // Verify sender exists
        userRepository.findById(senderId)
            .orElseThrow { RuntimeException("Sender not found") }

        // Verify receiver exists
        userRepository.findById(request.receiverId)
            .orElseThrow { RuntimeException("Receiver not found") }

        val message = Message(
            senderId = senderId,
            receiverId = request.receiverId,
            content = request.content,
            sentAt = LocalDateTime.now(),
            isRead = false
        )

        val savedMessage = messageRepository.save(message)
        return MessageMapper.toMessageResponse(savedMessage)
    }

    fun getConversation(otherUserId: UUID): ConversationResponse {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val currentUserId = userPrincipal.id

        // Verify other user exists
        userRepository.findById(otherUserId)
            .orElseThrow { RuntimeException("User not found") }

        val messages = messageRepository.findConversationBetweenUsers(currentUserId, otherUserId)
        val messageResponses = messages.map { MessageMapper.toMessageResponse(it) }

        // Get other user's name from profile
        val otherUserProfile = profileRepository.findByUserId(otherUserId)
        val otherUserName = otherUserProfile?.name

        return ConversationResponse(
            messages = messageResponses,
            otherUserId = otherUserId,
            otherUserName = otherUserName
        )
    }

    fun markMessageAsRead(messageId: UUID): MessageResponse {
        val userPrincipal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        val currentUserId = userPrincipal.id

        val message = messageRepository.findByIdAndReceiverId(messageId, currentUserId)
            ?: throw RuntimeException("Message not found or access denied")

        if (message.isRead) {
            return MessageMapper.toMessageResponse(message)
        }

        val updatedMessage = message.copy(isRead = true)
        val savedMessage = messageRepository.save(updatedMessage)
        return MessageMapper.toMessageResponse(savedMessage)
    }
} 