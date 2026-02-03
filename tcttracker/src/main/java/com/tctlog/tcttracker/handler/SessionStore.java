package com.tctlog.tcttracker.handler;

import com.tctlog.tcttracker.ModelDTO.AppSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {
    public final Map<String, AppSession> sessions = new ConcurrentHashMap<>();
}
