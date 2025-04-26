package com.collabedit.collabserver;

public class EditOperation {
    public String op;         // "insert" or "delete"
    public int uid;           // user ID
    public String clock;      // timestamp (e.g. "00:01")
    public String value;      // for insert only
    public String[] id;       // for delete only
    public String[] parent;   // for insert only

    @Override
    //shakl el jason el mstneeh
    public String toString() {
        return "EditOperation{" +
                "op='" + op + '\'' +
                ", uid=" + uid +
                ", clock='" + clock + '\'' +
                ", value='" + value + '\'' +
                ", id=" + (id != null ? "[" + String.join(",", id) + "]" : null) +
                ", parent=" + (parent != null ? "[" + String.join(",", parent) + "]" : null) +
                '}';
    }
}
