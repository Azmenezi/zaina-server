package com.zaina.zaina.websocket

import com.zaina.zaina.security.UserPrincipal
import com.zaina.zaina.service.WebSocketService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class WebSocketEventListener(
    private val webSocketService: WebSocketService
) {
    
    private val logger = LoggerFactory.getLogger(WebSocketEventListener::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        try {
            val accessor = StompHeaderAccessor.wrap(event.message)
            val authentication = accessor.user as? Authentication
            val sessionId = accessor.sessionId
            
            if (authentication != null) {
                val userPrincipal = authentication.principal as UserPrincipal
                
                logger.info("""
                    
                    ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                    ║ 🟢 WEBSOCKET SESSION CONNECTED - $timestamp
                    ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                    ║ Session ID: $sessionId
                    ║ User ID: ${userPrincipal.id}
                    ║ Username: ${userPrincipal.username}
                    ║ Email: ${userPrincipal.username}
                    ║ Role: ${userPrincipal.role}
                    ║ Authorities: ${authentication.authorities.joinToString(", ")}
                    ║ Remote Address: ${getRemoteAddress(accessor)}
                    ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                """.trimIndent())
                
                webSocketService.updateUserStatus(userPrincipal.id, true)
            } else {
                logger.warn("""
                    
                    ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                    ║ ⚠️ WEBSOCKET ANONYMOUS SESSION CONNECTED - $timestamp
                    ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                    ║ Session ID: $sessionId
                    ║ Warning: No authentication found for this session
                    ║ Remote Address: ${getRemoteAddress(accessor)}
                    ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                """.trimIndent())
            }
        } catch (e: Exception) {
            logger.error("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ ❌ WEBSOCKET CONNECT EVENT ERROR - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ Error: ${e.message}
                ║ Stack Trace: ${e.stackTraceToString().take(300)}
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
        }
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        try {
            val accessor = StompHeaderAccessor.wrap(event.message)
            val authentication = accessor.user as? Authentication
            val sessionId = accessor.sessionId
            val closeStatus = event.closeStatus
            
            if (authentication != null) {
                val userPrincipal = authentication.principal as UserPrincipal
                
                logger.info("""
                    
                    ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                    ║ 🔴 WEBSOCKET SESSION DISCONNECTED - $timestamp
                    ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                    ║ Session ID: $sessionId
                    ║ User ID: ${userPrincipal.id}
                    ║ Username: ${userPrincipal.username}
                    ║ Email: ${userPrincipal.username}
                    ║ Close Status: ${closeStatus?.code} - ${closeStatus?.reason ?: "Normal closure"}
                    ║ Remote Address: ${getRemoteAddress(accessor)}
                    ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                """.trimIndent())
                
                webSocketService.updateUserStatus(userPrincipal.id, false)
            } else {
                logger.info("""
                    
                    ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                    ║ 🔴 WEBSOCKET ANONYMOUS SESSION DISCONNECTED - $timestamp
                    ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                    ║ Session ID: $sessionId
                    ║ Close Status: ${closeStatus?.code} - ${closeStatus?.reason ?: "Normal closure"}
                    ║ Remote Address: ${getRemoteAddress(accessor)}
                    ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                """.trimIndent())
            }
        } catch (e: Exception) {
            logger.error("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ ❌ WEBSOCKET DISCONNECT EVENT ERROR - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ Error: ${e.message}
                ║ Stack Trace: ${e.stackTraceToString().take(300)}
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
        }
    }

    private fun getRemoteAddress(accessor: StompHeaderAccessor): String {
        return try {
            accessor.sessionAttributes?.get("remoteAddress")?.toString() ?: "Unknown"
        } catch (e: Exception) {
            "Unable to determine"
        }
    }
} 