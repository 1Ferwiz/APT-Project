package com.collabedit.collabserver;

import java.util.ArrayList;
import java.util.List;

public class CRDTNode {
    public String value;
    public int uid;
    public String clock;
    public boolean deleted;
    public List<CRDTNode> children;

    public CRDTNode(String value, int uid, String clock) {
        this.value = value;
        this.uid = uid;
        this.clock = clock;
        this.deleted = false;
        this.children = new ArrayList<>();
    }

    @Override
    public String toString() {
        return (deleted ? "" : value);
    }
}
