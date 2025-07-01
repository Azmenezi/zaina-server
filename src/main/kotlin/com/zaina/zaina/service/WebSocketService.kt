package com.zaina.zaina.service

import com.zaina.zaina.dto.*
import com.zaina.zaina.repository.ProfileRepository
import com.zaina.zaina.security.UserPrincipal
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class WebSocketService(
    private val messagingTemplate: SimpMessagingTemplate,
    private val profileRepository: ProfileRepository
) {
    
    private val logger = LoggerFactory.getLogger(WebSocketService::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
    
    // Track online users
    private val onlineUsers = ConcurrentHashMap<UUID, LocalDateTime>()
    
    fun sendMessageToUser(messageResponse: MessageResponse) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        try {
            // Get sender's name for display
            val senderProfile = profileRepository.findByUserId(messageResponse.senderId)
            val senderName = senderProfile?.name
            
            val chatMessage = ChatMessage(
                id = messageResponse.id,
                senderId = messageResponse.senderId,
                receiverId = messageResponse.receiverId,
                content = messageResponse.content,
                sentAt = messageResponse.sentAt,
                senderName = senderName
            )
            
            val webSocketMessage = WebSocketMessage(
                type = MessageType.CHAT_MESSAGE,
                data = chatMessage
            )
            
            logger.info("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ 💬 WEBSOCKET CHAT MESSAGE BROADCAST - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ Message ID: ${messageResponse.id}
                ║ From: ${messageResponse.senderId} (${senderName ?: "Unknown"})
                ║ To: ${messageResponse.receiverId}
                ║ Content Length: ${messageResponse.content.length} characters
                ║ Content Preview: ${messageResponse.content.take(50)}${if (messageResponse.content.length > 50) "..." else ""}
                ║ Sent At: ${messageResponse.sentAt}
                ║ Destination: /user/${messageResponse.receiverId}/queue/messages
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
            
            // Send to specific user
            messagingTemplate.convertAndSendToUser(
                messageResponse.receiverId.toString(),
                "/queue/messages",
                webSocketMessage
            )
            
            // Also send to sender for confirmation
            messagingTemplate.convertAndSendToUser(
                messageResponse.senderId.toString(),
                "/queue/messages",
                webSocketMessage
            )
            
            logger.info("✅ Message successfully broadcast to both sender and receiver via WebSocket")
            
        } catch (e: Exception) {
            logger.error("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ ❌ WEBSOCKET MESSAGE BROADCAST ERROR - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ Message ID: ${messageResponse.id}
                ║ From: ${messageResponse.senderId}
                ║ To: ${messageResponse.receiverId}
                ║ Error: ${e.message}
                ║ Stack Trace: ${e.stackTraceToString().take(300)}
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
        }
    }
    
    fun sendTypingIndicator(typingIndicator: TypingIndicator) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        try {
            val webSocketMessage = WebSocketMessage(
                type = MessageType.TYPING_INDICATOR,
                data = typingIndicator
            )
            
            logger.info("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ ⌨️ WEBSOCKET TYPING INDICATOR - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ From: ${typingIndicator.senderId} (${typingIndicator.senderName ?: "Unknown"})
                ║ To: ${typingIndicator.receiverId}
                ║ Typing: ${typingIndicator.typing}
                ║ Destination: /user/${typingIndicator.receiverId}/queue/typing
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
            
            messagingTemplate.convertAndSendToUser(
                typingIndicator.receiverId.toString(),
                "/queue/typing",
                webSocketMessage
            )
            
        } catch (e: Exception) {
            logger.error("❌ Error sending typing indicator via WebSocket: ${e.message}")
        }
    }
    
    fun sendReadReceipt(messageResponse: MessageResponse, readBy: UUID) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        try {
            val readReceipt = MessageReadReceipt(
                messageId = messageResponse.id,
                readBy = readBy,
                readAt = LocalDateTime.now()
            )
            
            val webSocketMessage = WebSocketMessage(
                type = MessageType.MESSAGE_READ_RECEIPT,
                data = readReceipt
            )
            
            logger.info("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ ✓ WEBSOCKET READ RECEIPT - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ Message ID: ${messageResponse.id}
                ║ Read By: $readBy
                ║ Original Sender: ${messageResponse.senderId}
                ║ Read At: ${readReceipt.readAt}
                ║ Destination: /user/${messageResponse.senderId}/queue/read-receipts
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
            
            messagingTemplate.convertAndSendToUser(
                messageResponse.senderId.toString(),
                "/queue/read-receipts",
                webSocketMessage
            )
            
        } catch (e: Exception) {
            logger.error("❌ Error sending read receipt via WebSocket: ${e.message}")
        }
    }
    
    fun updateUserStatus(userId: UUID, online: Boolean) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        try {
            val now = LocalDateTime.now()
            val previousStatus = onlineUsers.containsKey(userId)
            
            if (online) {
                onlineUsers[userId] = now
            } else {
                onlineUsers.remove(userId)
            }
            
            val userStatus = UserStatus(
                userId = userId,
                online = online,
                lastSeen = if (online) null else now
            )
            
            val webSocketMessage = WebSocketMessage(
                type = MessageType.USER_STATUS,
                data = userStatus
            )
            
            logger.info("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ 👤 WEBSOCKET USER STATUS UPDATE - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ User ID: $userId
                ║ Status Change: ${if (previousStatus) "Online" else "Offline"} → ${if (online) "Online" else "Offline"}
                ║ Last Seen: ${if (online) "Currently active" else now}
                ║ Total Online Users: ${onlineUsers.size}
                ║ Destination: /topic/user-status (broadcast)
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
            
            // Broadcast status to all users (you might want to limit this to connected users)
            messagingTemplate.convertAndSend("/topic/user-status", webSocketMessage)
            
        } catch (e: Exception) {
            logger.error("❌ Error updating user status via WebSocket: ${e.message}")
        }
    }
    
    fun sendConnectionNotification(connectionResponse: ConnectionResponse) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        try {
            val webSocketMessage = WebSocketMessage(
                type = MessageType.CONNECTION_REQUEST,
                data = connectionResponse
            )
            
            logger.info("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ 🤝 WEBSOCKET CONNECTION NOTIFICATION - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ Connection ID: ${connectionResponse.id}
                ║ Type: ${connectionResponse.type}
                ║ Status: ${connectionResponse.status}
                ║ From: ${connectionResponse.requesterId} (${connectionResponse.requesterName ?: "Unknown"})
                ║ To: ${connectionResponse.targetId} (${connectionResponse.targetName ?: "Unknown"})
                ║ Requested At: ${connectionResponse.requestedAt}
                ║ Destination: /user/${connectionResponse.targetId}/queue/connections
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
            
            // Send to target user
            messagingTemplate.convertAndSendToUser(
                connectionResponse.targetId.toString(),
                "/queue/connections",
                webSocketMessage
            )
            
            // If accepted, also notify requester
            if (connectionResponse.status.name == "ACCEPTED") {
                val acceptedMessage = WebSocketMessage(
                    type = MessageType.CONNECTION_ACCEPTED,
                    data = connectionResponse
                )
                
                logger.info("""
                    
                    ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                    ║ ✅ WEBSOCKET CONNECTION ACCEPTED NOTIFICATION - $timestamp
                    ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                    ║ Connection ID: ${connectionResponse.id}
                    ║ Notifying Requester: ${connectionResponse.requesterId}
                    ║ Destination: /user/${connectionResponse.requesterId}/queue/connections
                    ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                """.trimIndent())
                
                messagingTemplate.convertAndSendToUser(
                    connectionResponse.requesterId.toString(),
                    "/queue/connections",
                    acceptedMessage
                )
            }
            
        } catch (e: Exception) {
            logger.error("❌ Error sending connection notification via WebSocket: ${e.message}")
        }
    }
    
    fun getOnlineUsers(): Map<UUID, LocalDateTime> {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        logger.debug("📊 Online users requested at $timestamp - Total: ${onlineUsers.size}")
        return onlineUsers.toMap()
    }
    
    fun isUserOnline(userId: UUID): Boolean {
        val isOnline = onlineUsers.containsKey(userId)
        logger.debug("👤 User status check for $userId: ${if (isOnline) "Online" else "Offline"}")
        return isOnline
    }
    
    fun getUserLastSeen(userId: UUID): LocalDateTime? {
        val lastSeen = onlineUsers[userId]
        logger.debug("🕐 Last seen for $userId: ${lastSeen ?: "Never or offline"}")
        return lastSeen
    }
} 