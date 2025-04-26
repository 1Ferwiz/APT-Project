package com.collabedit.collabserver;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;//3lshan kol thread tb2a leeha 3laka bkol user
//3lshan mmkn user y2fl w ba2eet el user by2o aw yktbo aw kda
//zai room kdaa y3ny document
public class CollabSession {
    private final String code;//lkol document leeh code mo3ayn
    private final List<WebSocketSession> users;
    //each websocketsession y3ny one user btgm3 feeha kol el users

    public CollabSession(String code) {
        this.code = code;
        this.users = new CopyOnWriteArrayList<>();//list fadya
    }

    public String getCode() {
        return code;
    }

    public void addUser(WebSocketSession session) {
        users.add(session);
    }

    public void removeUser(WebSocketSession session) {
        users.remove(session);
    }

    public List<WebSocketSession> getUsers() {
        return users;
    }
}
