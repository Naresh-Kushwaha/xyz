package com.navratri.app.config;

import com.navratri.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * STOMP over WebSocket (with SockJS fallback) for real-time chat.
 * Clients connect to /ws, subscribe to /topic/conversations/{id} to receive messages,
 * and send to /app/chat.send to post a new one. Auth: the client passes its JWT access
 * token as a query param (?token=...) on the initial handshake; see ChatHandshakeInterceptor.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new com.navratri.app.chat.websocket.ChatHandshakeInterceptor(jwtService))
                .withSockJS();
    }
}
