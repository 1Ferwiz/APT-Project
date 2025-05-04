package com.collabedit.collabserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class CursorHandler extends TextWebSocketHandler {

    // Stores the last cursor+selection update for each user
    private final Map<String, CursorSelectionUpdateMessage> userCursors = new ConcurrentHashMap<>();
    // Tracks all active WebSocket sessions by userId
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // JSON mapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        sessions.put(userId, session);
        System.out.println("User " + userId + " connected.");

        // ── Send the existing cursor snapshot to this newcomer
        sendExistingCursorsToNewJoiner(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String messagePayload = message.getPayload();
        CursorSelectionUpdateMessage updateMessage =
                objectMapper.readValue(messagePayload, CursorSelectionUpdateMessage.class);

        if ("cursor_selection_update".equals(updateMessage.getOp())) {
            String userId = getUserIdFromSession(session);
            userCursors.put(userId, updateMessage);
            broadcastCursorPositions();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        sessions.remove(userId);
        userCursors.remove(userId);
        System.out.println("User " + userId + " disconnected.");

        // ── If no users remain, clean up all cursor data
        cleanupSessionIfEmpty();
    }

    // Broadcasts the current map of all users' cursor+selection states
    private void broadcastCursorPositions() {
        try {
            String cursorPositionsMessage = objectMapper.writeValueAsString(userCursors);
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(cursorPositionsMessage));
                }
            }
        } catch (Exception e) {
            System.out.println("Error broadcasting cursor positions: " + e.getMessage());
        }
    }

    /**
     * Send the full snapshot of existing cursor/selection states
     * to a newly-connected session.
     */
    private void sendExistingCursorsToNewJoiner(WebSocketSession session) {
        try {
            String snapshot = objectMapper.writeValueAsString(userCursors);
            session.sendMessage(new TextMessage(snapshot));
        } catch (Exception e) {
            System.out.println("Error sending initial cursor snapshot: " + e.getMessage());
        }
    }

    /**
     * If no sessions remain open, clear out the cursor map
     * to free memory.
     */
    private void cleanupSessionIfEmpty() {
        if (sessions.isEmpty()) {
            userCursors.clear();
            System.out.println("All users disconnected—cleared cursor data.");
        }
    }

    // Placeholder method to extract a userId; currently uses session ID
    private String getUserIdFromSession(WebSocketSession session) {
        return session.getId();
    }
}
