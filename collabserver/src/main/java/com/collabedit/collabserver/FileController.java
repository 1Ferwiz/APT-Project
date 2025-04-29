package com.collabedit.collabserver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController {

    // Simulated in-memory content store for now (just for testing)
    private static final Map<String, StringBuilder> sessionTextStore = new java.util.concurrent.ConcurrentHashMap<>();

    // ⬆️ Import: Receive full text and store it under session code
    @PostMapping("/import")
    public ResponseEntity<String> importText(@RequestParam String code, @RequestBody String content) {
        sessionTextStore.put(code, new StringBuilder(content));
        return ResponseEntity.ok("File imported successfully to session: " + code);
    }

    // ⬇️ Export: Return the current text of a session
    @GetMapping("/export")
    public ResponseEntity<String> exportText(@RequestParam String code) {
        StringBuilder content = sessionTextStore.get(code);
        if (content == null) {
            return ResponseEntity.status(404).body("No content found for session: " + code);
        }
        return ResponseEntity.ok(content.toString());
    }
}
