package com.collabedit.collabserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/file")
public class FileController {

   @Autowired
    private SessionBroadcaster sessionBroadcaster;

    // ⬆️ Import: Receive full text and store it under session code
    @PostMapping("/import")
    public ResponseEntity<String> importText(@RequestParam String code, @RequestBody String content) {
        CollabSession session = SessionManager.getSession(code);
        if (session == null) {
            return ResponseEntity.status(404).body("Session not found: " + code);
        }

        // Update session's document content
        session.setDocumentContent(content);

        // Broadcast new content to all users
        sessionBroadcaster.broadcastDocumentContent(code, content);

        return ResponseEntity.ok("File imported successfully to session: " + code);
    }

    // ⬇️ Export: Return the current text of a session
    @GetMapping("/export")
    public ResponseEntity<String> exportText(@RequestParam String code) {
        CollabSession session = SessionManager.getSession(code);
        if (session == null) {
            return ResponseEntity.status(404).body("No content found for session: " + code);
        }

        return ResponseEntity.ok(session.getDocumentContent());
    }
}
