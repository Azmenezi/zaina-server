package com.zaina.zaina.websocket

import com.zaina.zaina.security.CustomUserDetailsService
import com.zaina.zaina.security.JwtUtils
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class WebSocketAuthChannelInterceptor(
    private val jwtUtils: JwtUtils,
    private val userDetailsService: CustomUserDetailsService
) : ChannelInterceptor {
    
    private val logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor::class.java)
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
        val timestamp = LocalDateTime.now().format(dateFormatter)
        
        if (accessor != null) {
            val command = accessor.command
            val sessionId = accessor.sessionId
            val destination = accessor.destination
            
            // Log all WebSocket commands
            when (command) {
                StompCommand.CONNECT -> {
                    logger.info("""
                        
                        ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                        ║ 🔌 WEBSOCKET CONNECT ATTEMPT - $timestamp
                        ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                        ║ Session ID: $sessionId
                        ║ Command: $command
                        ║ Headers: ${accessor.toNativeHeaderMap()}
                        ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                    """.trimIndent())
                    
                    handleAuthentication(accessor, sessionId, timestamp)
                }
                
                StompCommand.SUBSCRIBE -> {
                    logger.info("""
                        
                        ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                        ║ 📝 WEBSOCKET SUBSCRIPTION - $timestamp
                        ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                        ║ Session ID: $sessionId
                        ║ Destination: $destination
                        ║ User: ${accessor.user?.name ?: "Anonymous"}
                        ║ Subscription ID: ${accessor.subscriptionId}
                        ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                    """.trimIndent())
                }
                
                StompCommand.SEND -> {
                    logger.info("""
                        
                        ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                        ║ 📤 WEBSOCKET MESSAGE SEND - $timestamp
                        ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                        ║ Session ID: $sessionId
                        ║ Destination: $destination
                        ║ User: ${accessor.user?.name ?: "Anonymous"}
                        ║ Message Size: ${message.payload?.toString()?.length ?: 0} characters
                        ║ Payload Preview: ${getPayloadPreview(message.payload)}
                        ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                    """.trimIndent())
                }
                
                StompCommand.UNSUBSCRIBE -> {
                    logger.info("""
                        
                        ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                        ║ 🚫 WEBSOCKET UNSUBSCRIPTION - $timestamp
                        ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                        ║ Session ID: $sessionId
                        ║ Subscription ID: ${accessor.subscriptionId}
                        ║ User: ${accessor.user?.name ?: "Anonymous"}
                        ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                    """.trimIndent())
                }
                
                StompCommand.DISCONNECT -> {
                    logger.info("""
                        
                        ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                        ║ 🔌 WEBSOCKET DISCONNECT - $timestamp
                        ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                        ║ Session ID: $sessionId
                        ║ User: ${accessor.user?.name ?: "Anonymous"}
                        ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                    """.trimIndent())
                }
                
                else -> {
                    logger.debug("WebSocket command: $command for session: $sessionId")
                }
            }
        }
        
        return message
    }

    private fun handleAuthentication(accessor: StompHeaderAccessor, sessionId: String?, timestamp: String) {
        try {
            val authToken = accessor.getFirstNativeHeader("Authorization")
            
            if (authToken != null && authToken.startsWith("Bearer ")) {
                val jwt = authToken.substring(7)
                
                if (jwtUtils.validateJwtToken(jwt)) {
                    val username = jwtUtils.getUsernameFromJwtToken(jwt)
                    val userId = jwtUtils.getUserIdFromJwtToken(jwt)
                    val userDetails = userDetailsService.loadUserByUsername(username)
                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    
                    accessor.user = authentication
                    SecurityContextHolder.getContext().authentication = authentication
                    
                    logger.info("""
                        
                        ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                        ║ ✅ WEBSOCKET AUTHENTICATION SUCCESS - $timestamp
                        ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                        ║ Session ID: $sessionId
                        ║ Username: $username
                        ║ User ID: $userId
                        ║ Authorities: ${userDetails.authorities.joinToString(", ")}
                        ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                    """.trimIndent())
                } else {
                    logger.warn("""
                        
                        ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                        ║ ❌ WEBSOCKET AUTHENTICATION FAILED - $timestamp
                        ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                        ║ Session ID: $sessionId
                        ║ Reason: Invalid JWT token
                        ║ Token Preview: ${jwt.take(20)}...
                        ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                    """.trimIndent())
                }
            } else {
                logger.warn("""
                    
                    ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                    ║ ⚠️ WEBSOCKET AUTHENTICATION MISSING - $timestamp
                    ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                    ║ Session ID: $sessionId
                    ║ Reason: No Authorization header found or invalid format
                    ║ Expected: Bearer <token>
                    ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
                """.trimIndent())
            }
        } catch (e: Exception) {
            logger.error("""
                
                ╔══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗
                ║ 💥 WEBSOCKET AUTHENTICATION ERROR - $timestamp
                ╠══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣
                ║ Session ID: $sessionId
                ║ Error: ${e.message}
                ║ Stack Trace: ${e.stackTraceToString().take(500)}
                ╚══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝
            """.trimIndent())
        }
    }

    private fun getPayloadPreview(payload: Any?): String {
        return when (payload) {
            null -> "null"
            is String -> if (payload.length > 100) payload.take(100) + "..." else payload
            is ByteArray -> "Binary data (${payload.size} bytes)"
            else -> payload.toString().let { if (it.length > 100) it.take(100) + "..." else it }
        }
    }
} 