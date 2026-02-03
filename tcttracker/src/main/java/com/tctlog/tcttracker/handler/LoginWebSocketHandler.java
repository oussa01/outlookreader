package com.tctlog.tcttracker.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tctlog.tcttracker.ModelDTO.AppSession;
import com.tctlog.tcttracker.ModelDTO.LoginPath;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
@Component
public class LoginWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Store active sessions
    private final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    // Send LoginPath DTO to all connected clients
    public void sendLoginPath(LoginPath loginPath) {
        try {
            String payload = objectMapper.writeValueAsString(loginPath);
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendSessionToken(AppSession appSession){
        try {
            String payload = objectMapper.writeValueAsString(appSession);
            for (WebSocketSession session : sessions){
                session.sendMessage(new TextMessage(payload));
            }
        } catch  (IOException e) {
            e.printStackTrace();
        }
    }
}
