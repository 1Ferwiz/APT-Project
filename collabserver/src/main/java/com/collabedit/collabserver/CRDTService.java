package com.collabedit.collabserver;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class CRDTService {

    private final Map<String, CRDTDocument> documents = new ConcurrentHashMap<>();

    // Create or get document for a session
    public CRDTDocument getOrCreateDocument(String sessionCode) {
        return documents.computeIfAbsent(sessionCode, k -> new CRDTDocument());
    }

    // Insert operation
    public void insert(String sessionCode, EditOperation op) {
        CRDTDocument doc = getOrCreateDocument(sessionCode);
        doc.insert(op);
    }

    // Delete operation
    public void delete(String sessionCode, EditOperation op) {
        CRDTDocument doc = getOrCreateDocument(sessionCode);
        doc.delete(op);
    }

    // Export document
    public String exportDocument(String sessionCode) {
        CRDTDocument doc = documents.get(sessionCode);
        if (doc == null) {
            return "";
        }
        return doc.exportText();
    }
}
