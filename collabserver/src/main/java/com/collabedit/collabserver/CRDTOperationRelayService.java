package com.collabedit.collabserver;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
public class CRDTOperationRelayService {

    public void relayOperation(EditOperation operation, String sessionCode, TextMessage rawMessage) {
        CollabSession session = SessionManager.getSession(sessionCode);

        if (session == null) {
            System.out.println("Session not found for code: " + sessionCode);
            return;
        }

        for (WebSocketSession s : session.getUsers().values()) {
            try {
                if (s.isOpen()) {
                    s.sendMessage(rawMessage);  // Relay the original message
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
