package com.navratri.app.chat.websocket;

import com.navratri.app.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Validates the JWT access token passed as ?token=... on the WebSocket handshake
 * (browsers cannot set Authorization headers on the initial SockJS handshake request),
 * and stashes the authenticated user's email in the WebSocket session attributes so
 * ChatWebSocketController can identify the sender of each message.
 */
@RequiredArgsConstructor
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                    @NonNull WebSocketHandler wsHandler, @NonNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");
            if (token != null && jwtService.isTokenValid(token) && "access".equals(jwtService.extractTokenType(token))) {
                attributes.put("email", jwtService.extractEmail(token));
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response,
                                @NonNull WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
