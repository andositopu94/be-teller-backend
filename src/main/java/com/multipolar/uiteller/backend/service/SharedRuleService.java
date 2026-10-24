package com.multipolar.uiteller.backend.service;
import com.multipolar.uiteller.backend.model.converter.Rule;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class SharedRuleService {
    private final Map<String, Rule> context = new ConcurrentHashMap<>();
    public void set(String key, Rule value) {
        context.put(key, value);
    }
    public Rule get(String key) {
        return context.get(key);
    }
    public Boolean hasContains(String key) {
        if(get(key) !=null){return true;}else {return false;}
    }
    public void remove(String key) {
        context.remove(key);
    }
    @Scheduled(cron = "0 0 0 * * *")
    public void clear() {
        context.clear();
    }
}