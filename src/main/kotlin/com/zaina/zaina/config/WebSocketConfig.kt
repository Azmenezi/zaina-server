package com.zaina.zaina.config

import com.zaina.zaina.websocket.WebSocketAuthChannelInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val webSocketAuthChannelInterceptor: WebSocketAuthChannelInterceptor
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // Enable in-memory message broker with destination prefixes
        config.enableSimpleBroker("/topic", "/queue")
        // Set application destination prefix
        config.setApplicationDestinationPrefixes("/app")
        // Set user destination prefix for private messages
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // Register STOMP endpoint for WebSocket connections
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        // Add authentication interceptor
        registration.interceptors(webSocketAuthChannelInterceptor)
    }
} 