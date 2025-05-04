package com.collabedit.collabserver;

import java.util.Map;

public class ClientMessage {
    public String type; // "edit", "cursor", etc.
    public int uid;
    public Map<String, Object> payload; // holds position or edit fields

    @Override
    public String toString() {
        return "ClientMessage{type='" + type + "', uid=" + uid + ", payload=" + payload+"}";
    }
}
