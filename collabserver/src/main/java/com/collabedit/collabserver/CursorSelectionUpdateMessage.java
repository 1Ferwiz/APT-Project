package com.collabedit.collabserver;

public class CursorSelectionUpdateMessage {
    private String op;
    private String userId;
    private Position cursor;
    private Position selectionStart;
    private Position selectionEnd;

    public String getOp() { return op; }
    public void setOp(String op) { this.op = op; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Position getCursor() { return cursor; }
    public void setCursor(Position cursor) { this.cursor = cursor; }

    public Position getSelectionStart() { return selectionStart; }
    public void setSelectionStart(Position selectionStart) { this.selectionStart = selectionStart; }

    public Position getSelectionEnd() { return selectionEnd; }
    public void setSelectionEnd(Position selectionEnd) { this.selectionEnd = selectionEnd; }
}
