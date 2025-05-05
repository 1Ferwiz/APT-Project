package com.collabedit.collabserver;

import java.util.Arrays;

public class EditOperation {
    public String op; // "insert" or "delete"
    public String value; // Only for inserts
    public int uid;
    public String clock;
    public String[] parent; // clock of the parent
    public String[] id; // unique ID of this char (clock + uid)

    public EditOperation(){}
    public EditOperation(String op, String value) {
        this.op = op;
        this.value = value;
    }
    @Override
    public String toString() {
        return "EditOperation{" +
                "op='" + op + '\'' +
                ", value='" + value + '\'' +
                ", uid=" + uid +
                ", clock='" + clock + '\'' +
                ", parent=" + Arrays.toString(parent) +
                ", id=" + Arrays.toString(id) +
                '}';
    }
}


