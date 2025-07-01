package com.zaina.zaina.websocket

import com.zaina.zaina.dto.*
import com.zaina.zaina.security.UserPrincipal
import com.zaina.zaina.service.MessageService
import com.zaina.zaina.service.WebSocketService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import java.security.Principal
import java.util.*

@Controller
class ChatWebSocketController(
    private val messageService: MessageService,
    private val webSocketService: WebSocketService
) {
    
    private val logger = LoggerFactory.getLogger(ChatWebSocketController::class.java)

    @MessageMapping("/chat.send")
    fun sendMessage(@Payload request: SendMessageRequest, principal: Principal) {
        try {
            val authentication = principal as Authentication
            val userPrincipal = authentication.principal as UserPrincipal
            
            logger.info("Received message from user ${userPrincipal.id} to ${request.receiverId}")
            
            // Save message to database
            val messageResponse = messageService.sendMessage(request)
            
            // Send real-time message to receiver
            webSocketService.sendMessageToUser(messageResponse)
            
        } catch (e: Exception) {
            logger.error("Error sending message: ${e.message}")
        }
    }

    @MessageMapping("/chat.typing")
    fun handleTypingIndicator(@Payload typingIndicator: TypingIndicator, principal: Principal) {
        try {
            val authentication = principal as Authentication
            val userPrincipal = authentication.principal as UserPrincipal
            
            // Update typing indicator with sender info
            val updatedIndicator = typingIndicator.copy(
                senderId = userPrincipal.id,
                senderName = userPrincipal.username
            )
            
            // Send typing indicator to receiver
            webSocketService.sendTypingIndicator(updatedIndicator)
            
        } catch (e: Exception) {
            logger.error("Error handling typing indicator: ${e.message}")
        }
    }

    @MessageMapping("/chat.markRead")
    fun markMessageAsRead(@Payload messageId: UUID, principal: Principal) {
        try {
            val authentication = principal as Authentication
            val userPrincipal = authentication.principal as UserPrincipal
            
            // Mark message as read
            val updatedMessage = messageService.markMessageAsRead(messageId)
            
            // Send read receipt to sender
            webSocketService.sendReadReceipt(updatedMessage, userPrincipal.id)
            
        } catch (e: Exception) {
            logger.error("Error marking message as read: ${e.message}")
        }
    }

    @MessageMapping("/user.status")
    @SendToUser("/queue/status")
    fun updateUserStatus(principal: Principal): String {
        try {
            val authentication = principal as Authentication
            val userPrincipal = authentication.principal as UserPrincipal
            
            // Update user status to online
            webSocketService.updateUserStatus(userPrincipal.id, true)
            
            return "Status updated"
        } catch (e: Exception) {
            logger.error("Error updating user status: ${e.message}")
            return "Error"
        }
    }
} 