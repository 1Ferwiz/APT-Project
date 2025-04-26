package com.collabedit.collabserver;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CollabWebSocketHandler extends TextWebSocketHandler {


    @Autowired
    private CRDTOperationRelayService crdtOperationRelayService;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            // Extract the session code from the URL
            String uri = session.getUri().toString();  // Example: ws://localhost:8080/ws/edit?code=abc123
            String code = null;

            if (uri.contains("?code=")) {
                code = uri.split("\\?code=")[1];
            }

            if (code == null || code.isEmpty()) {
                System.out.println("Connection rejected: No session code provided");
                session.close();
                return;
            }

            // Get or create the session and add this user
            CollabSession collabSession = SessionManager.getOrCreateSession(code);
            collabSession.addUser(session);

            // Attach session code to this WebSocket for deleting the user after connection is closed
            session.getAttributes().put("code", code);

            System.out.println("User connected to session '" + code + "': " + session.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Raw message: " + payload);

        // Get the session code for this user
        String code = (String) session.getAttributes().get("code");
        if (code == null) {
            System.out.println("No session code found for user: " + session.getId());
            return;
        }

        try {
            // Parse the JSON into EditOperation
            EditOperation op = new com.google.gson.Gson().fromJson(payload, EditOperation.class);
            System.out.println("Parsed operation: " + op.toString());

            // Relay the operation to other users via the Service
            crdtOperationRelayService.relayOperation(op, code, message);

        } catch (Exception e) {
            System.out.println("Invalid message format: " + e.getMessage());
        }
    }




    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String code = (String) session.getAttributes().get("code");

        if (code == null) {
            System.out.println("Disconnected user had no session code: " + session.getId());
            return;
        }

        CollabSession collabSession = SessionManager.getSession(code);
        if (collabSession != null) {
            collabSession.removeUser(session);//bysheel kol el users
            System.out.println("User " + session.getId() + " removed from session '" + code + "'");

            if (collabSession.getUsers().isEmpty()) {
                SessionManager.removeSession(code);
                System.out.println("Session '" + code + "' is now empty and has been removed.");
            }
        } else {
            System.out.println("Session not found for disconnected user: " + session.getId());
        }
    }

}
