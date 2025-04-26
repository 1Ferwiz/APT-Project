package com.collabedit.collabserver;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CollabWebSocketHandler extends TextWebSocketHandler {


    @Autowired
    private CRDTOperationRelayService crdtOperationRelayService;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String code = null;
        try {
            String uri = session.getUri().toString();  // Example: ws://localhost:8080/ws/edit?code=edit-xxxx


            if (uri.contains("?code=")) {
                code = uri.split("\\?code=")[1];
            }

            if (code == null || code.isEmpty()) {
                System.out.println("Connection rejected: No session code provided");
                session.close();
                return;
            }

            CollabSession collabSession = SessionManager.getOrCreateSession(code);
            collabSession.addUser(session);

            session.getAttributes().put("code", code);

            // 🔥 New code: detect role
            if (code.startsWith("edit-")) {
                session.getAttributes().put("role", "editor");
            } else if (code.startsWith("view-")) {
                session.getAttributes().put("role", "viewer");
            } else {
                System.out.println("Invalid code format: " + code);
                session.close();
            }

            System.out.println("User connected with role: " + session.getAttributes().get("role") + " | ID: " + session.getId());
                    // Notify others in the session about this user's join
            CollabSession sessionRoom = SessionManager.getSession(code);
            if (sessionRoom != null) {
                String joinMsg = new com.google.gson.Gson().toJson(Map.of(
                        "type", "join",
                        "uid", session.getId()
                ));

                for (WebSocketSession s : sessionRoom.getUsers()) {
                    if (s.isOpen() && !s.getId().equals(session.getId())) {
                        s.sendMessage(new TextMessage(joinMsg));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Raw message: " + payload);

        String code = (String) session.getAttributes().get("code");
        String role = (String) session.getAttributes().get("role");

        if (code == null || role == null) {
            System.out.println("Missing session code or role for user: " + session.getId());
            return;
        }

        try {
            // Parse the message into a ClientMessage (supports type: "edit", "cursor")
            ClientMessage clientMsg = new com.google.gson.Gson().fromJson(payload, ClientMessage.class);
            System.out.println("Parsed message: " + clientMsg.toString());

            if (clientMsg.type.equals("edit")) {
                if (!role.equals("editor")) {
                    System.out.println("Viewer attempted to edit: " + session.getId());
                    return;
                }

                // You could also extract more specific edit fields here if needed
                crdtOperationRelayService.relayOperation(null, code, message); // for now pass null for EditOperation

            } else if (clientMsg.type.equals("cursor")) {
                // Just broadcast the message to others in the session
                CollabSession collabSession = SessionManager.getSession(code);
                if (collabSession != null) {
                    for (WebSocketSession s : collabSession.getUsers()) {
                        if (s.isOpen() && !s.getId().equals(session.getId())) {
                            s.sendMessage(message); // Relay cursor position to others
                        }
                    }
                }
            } else {
                System.out.println("Unknown message type: " + clientMsg.type);
            }

        } catch (Exception e) {
            System.out.println("Failed to parse client message: " + e.getMessage());
        }
    }






    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
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
        if (collabSession != null) {
            collabSession.removeUser(session);
            System.out.println("User " + session.getId() + " removed from session '" + code + "'");

            // 🔥 Broadcast leave event
            String leaveMsg = new com.google.gson.Gson().toJson(Map.of(
                    "type", "leave",
                    "uid", session.getId()
            ));

            for (WebSocketSession s : collabSession.getUsers()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(leaveMsg));
                }
            }

            if (collabSession.getUsers().isEmpty()) {
                SessionManager.removeSession(code);
                System.out.println("Session '" + code + "' is now empty and has been removed.");
            }
        }

    }

}
