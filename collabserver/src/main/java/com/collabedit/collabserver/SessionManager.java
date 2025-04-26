package com.collabedit.collabserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, CollabSession> sessions = new ConcurrentHashMap<>();
    //map l kol el acive sessions btakhod el room w el session code

    public static CollabSession getOrCreateSession(String code) {
        return sessions.computeIfAbsent(code, CollabSession::new);
    }
    //el func btrg3 el room code w lw msh mawgood bt3ml room w tsend el code
    // w t3ml room gdeeda hya dy lazmt computeifabsent

    public static CollabSession getSession(String code) {
        return sessions.get(code);
    }

    public static void removeSession(String code) {
        sessions.remove(code);
    }

    public static Map<String, CollabSession> getAllSessions() {
        return sessions;
    }
}
