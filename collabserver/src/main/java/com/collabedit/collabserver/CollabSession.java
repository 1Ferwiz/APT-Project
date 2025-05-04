package com.collabedit.collabserver;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CollabSession {
    private final String code;  // لكل document له code معين

    // Map of users: userId -> WebSocketSession
    private final Map<String, WebSocketSession> users = new ConcurrentHashMap<>();

    // Per-user Undo/Redo manager map: userId -> UndoRedoManager
    private final Map<String, UndoRedoManager> userUndoManagers = new ConcurrentHashMap<>();

    // Stores the latest full document content (for import/export)
    private String documentContent = "";

    public CollabSession(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    // Add user to the session
    public void addUser(String userId, WebSocketSession session) {
        users.put(userId, session);
    }

    // Remove user from the session
    public void removeUser(String userId) {
        users.remove(userId);
    }

    // Get list of active user IDs
    public List<String> getActiveUserIds() {
        return users.keySet().stream().toList();
    }

    // Get the map of users (userId → session)
    public Map<String, WebSocketSession> getUsers() {
        return users;
    }

    // Get or create the UndoRedoManager for a user
    public UndoRedoManager getOrCreateUndoRedoManager(String userId) {
        return userUndoManagers.computeIfAbsent(userId, k -> new UndoRedoManager());
    }

    // Get the UndoRedoManager map
    public Map<String, UndoRedoManager> getUserUndoManagers() {
        return userUndoManagers;
    }

    // Set the full document content
    public void setDocumentContent(String content) {
        this.documentContent = content;
    }

    // Get the full document content
    public String getDocumentContent() {
        return documentContent;
    }
}
