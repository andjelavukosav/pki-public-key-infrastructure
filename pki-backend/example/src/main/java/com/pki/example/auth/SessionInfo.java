package com.pki.example.auth;

import lombok.Data;

import java.util.Date;

@Data
public class SessionInfo {

    private String sessionId;
    private String ipAddress;
    private String userAgent;
    private Date lastActivity;
    private Long userId;
    private String device;

    public SessionInfo() {
    }
    public SessionInfo(String sessionId, String ipAddress, String userAgent, Date lastActivity, Long userId, String device) {
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.lastActivity = lastActivity;
        this.userId = userId;
        this.device = device;
    }

}
