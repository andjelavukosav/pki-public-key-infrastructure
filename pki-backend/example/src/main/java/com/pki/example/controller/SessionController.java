package com.pki.example.controller;

import com.pki.example.auth.SessionInfo;
import com.pki.example.auth.SessionRegistry;
import com.pki.example.model.entity.User;
import com.pki.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final UserService userService;

    private  final SessionRegistry sessionRegistry;

    @GetMapping("")
    public ResponseEntity<List<SessionInfo>> getActiveSessions(Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        List<SessionInfo> sessions = sessionRegistry.getSessionsByUser(currentUser.getId());
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/revoke")
    public ResponseEntity<Void> revokeSession(@RequestParam String sessionId, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());

        sessionRegistry.revokeSession(sessionId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/revoke-all-others")
    public ResponseEntity<Void> revokeAllOtherSessions(
            @RequestParam String currentSessionId,
            Principal principal) {

        User currentUser = userService.findByEmail(principal.getName());

        sessionRegistry.revokeAllOtherSessions(currentUser.getId(), currentSessionId);
        return ResponseEntity.ok().build();
    }

}
