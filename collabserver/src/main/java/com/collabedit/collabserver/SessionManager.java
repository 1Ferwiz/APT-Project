package com.collabedit.collabserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, CollabSession> sessions = new ConcurrentHashMap<>();
    // Map to store live document content per session (for file export/import)
    private static final Map<String, StringBuilder> sessionDocuments = new ConcurrentHashMap<>();

    //map l kol el acive sessions btakhod el room w el session code
    public static CollabSession getOrCreateSession(String code) {
        sessions.computeIfAbsent(code, CollabSession::new);
        sessionDocuments.computeIfAbsent(code, k -> new StringBuilder());
        return sessions.get(code);
    }

    //el func btrg3 el room code w lw msh mawgood bt3ml room w tsend el code
    // w t3ml room gdeeda hya dy lazmt computeifabsent

    public static CollabSession getSession(String code) {
        return sessions.get(code);
    }

    public static void removeSession(String code) {
        sessions.remove(code);
        sessionDocuments.remove(code);
    }

    public static Map<String, CollabSession> getAllSessions() {
        return sessions;
    }

    // Get the current document content for exporting
    public static String getDocumentContent(String code) {
        StringBuilder sb = sessionDocuments.get(code);
        return (sb != null) ? sb.toString() : "";
    }

    // Set new document content for importing (overwrites previous)
    public static void setDocumentContent(String code, String newContent) {
        sessionDocuments.put(code, new StringBuilder(newContent));

        // Clear undo/redo for all users in session when file is imported
        CollabSession session = sessions.get(code);
        if (session != null) {
            for (UndoRedoManager undoRedo : session.getUserUndoManagers().values()) {
                undoRedo.clearStacks();
            }
        }
    }
}
