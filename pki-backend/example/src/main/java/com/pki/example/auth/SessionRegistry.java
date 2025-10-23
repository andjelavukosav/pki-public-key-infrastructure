package com.pki.example.auth;

import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SessionRegistry {

    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>(); // cuvamo samo u memoriji servera tj dok je server pokrenut

    public void addSession(SessionInfo session) {
        sessions.put(session.getSessionId(), session);
    }

    public List<SessionInfo> getSessionsByUser(Long userId) {
        return sessions.values().stream()
                .filter(s -> s.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void revokeSession(String sessionId, Long userId) {
        SessionInfo s = sessions.get(sessionId);
        if (s != null && s.getUserId().equals(userId)) {
            sessions.remove(sessionId);
        }
    }

    public void revokeAllOtherSessions(Long userId, String currentSessionId) {
        sessions.values().removeIf(s -> s.getUserId().equals(userId) && !s.getSessionId().equals(currentSessionId));
    }


    public void updateLastActivity(String sessionId) {
        SessionInfo s = sessions.get(sessionId);
        if (s != null) {
            s.setLastActivity(new Date());
        }
    }

    public boolean hasSession(String sessionId){
        return sessions.containsKey(sessionId);
    }

    public SessionInfo getSession(String sessionId) {
        return sessions.get(sessionId);
    }
}
