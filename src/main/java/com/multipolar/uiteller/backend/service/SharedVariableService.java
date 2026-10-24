package com.multipolar.uiteller.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SharedVariableService {
    private final Map<String, Long> context = new ConcurrentHashMap<>();

    public void set(String key, Long value) {
        context.put(key, value);
    }

    public Long get(String key) {
        return context.get(key);
    }

    public void remove(String key) {
        context.remove(key);
    }

    @Scheduled(fixedDelay = 70000) // run every 70s
    public void clear() {
        for (String key : context.keySet()) {
            if (System.currentTimeMillis() - context.get(key) > 70000) {
                context.remove(key);
                log.info(key + " context removed by scheduler");
            }
        }
    }
}
