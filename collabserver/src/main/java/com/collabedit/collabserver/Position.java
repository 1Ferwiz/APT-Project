package com.collabedit.collabserver;

public class Position {
    private int line;
    private int ch;

    public Position() {}  // Default constructor for JSON parsing

    public Position(int line, int ch) {
        this.line = line;
        this.ch = ch;
    }

    public int getLine() { return line; }
    public void setLine(int line) { this.line = line; }

    public int getCh() { return ch; }
    public void setCh(int ch) { this.ch = ch; }
}
