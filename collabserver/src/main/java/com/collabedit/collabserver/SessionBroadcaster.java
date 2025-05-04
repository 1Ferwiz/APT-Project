package com.collabedit.collabserver;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collection;

@Component
public class SessionBroadcaster {

    public void broadcastDocumentContent(String sessionCode, String content) {
        CollabSession session = SessionManager.getSession(sessionCode);
        if (session == null) return;

        Collection<WebSocketSession> users = session.getUsers().values();
        for (WebSocketSession user : users) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(new TextMessage(content));
                }
            } catch (Exception e) {
                System.out.println("Failed to send message to user: " + e.getMessage());
            }
        }
    }
}
