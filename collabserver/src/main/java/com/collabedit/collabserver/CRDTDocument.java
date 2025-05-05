package com.collabedit.collabserver;

import java.util.ArrayList;
import java.util.List;

public class CRDTDocument {
    private final CRDTNode root;

    public CRDTDocument() {
        root = new CRDTNode("", -1, "root");
    }

    // Insert a character
    public void insert(EditOperation op) {
        CRDTNode parent = findNodeByClock(root, op.parent);
        if (parent == null) {
            parent = root;
        }

        CRDTNode newNode = new CRDTNode(op.value, op.uid, op.clock);
        parent.children.add(newNode);
        System.out.println("Inserted: " + op.value + " after " + parent.clock);
    }

    // Delete a character
    public void delete(EditOperation op) {
        CRDTNode target = findNodeByClock(root, op.id);
        if (target != null) {
            target.deleted = true;
            System.out.println("Deleted: " + target.value + " (" + target.clock + ")");
        }
    }

    // Export the document as full text
    public String exportText() {
        StringBuilder sb = new StringBuilder();
        traverse(root, sb);
        return sb.toString();
    }

    // Helper: Traverse tree and build text
    private void traverse(CRDTNode node, StringBuilder sb) {
        if (!node.deleted) {
            sb.append(node.value);
        }
        for (CRDTNode child : node.children) {
            traverse(child, sb);
        }
    }

    // Helper: Find a node by its clock (and UID)
    private CRDTNode findNodeByClock(CRDTNode current, String[] clockInfo) {
        if (current.clock.equals(clockInfo[1])) {
            return current;
        }
        for (CRDTNode child : current.children) {
            CRDTNode found = findNodeByClock(child, clockInfo);
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
