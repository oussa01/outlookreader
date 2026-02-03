package com.tctlog.tcttracker.ModelDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Getter
@Setter
public class AppSession {
    private String type;
    private String sessionToken;
    private Instant expiration;
    private String userEmail;
    private boolean isAuthenticated;
    public final Map<String, AppSession> sessions = new ConcurrentHashMap<>();

}
