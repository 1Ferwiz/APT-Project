package com.collabedit.collabserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @GetMapping("/generate")
    public Map<String, String> generateSessionCodes() {
        String editorCode = "edit-" + UUID.randomUUID().toString();
        String viewerCode = "view-" + UUID.randomUUID().toString();

        Map<String, String> codes = new HashMap<>();
        codes.put("editor", editorCode);
        codes.put("viewer", viewerCode);

        return codes;
    }
}
