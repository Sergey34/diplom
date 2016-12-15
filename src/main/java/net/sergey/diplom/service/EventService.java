package net.sergey.diplom.service;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventService {
    private final Map<String, Integer> progressMap = new ConcurrentHashMap<>();
    private final List<SseEmitter> emitters = new ArrayList<>();

    public List<SseEmitter> getEmitters() {
        return emitters;
    }

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }

    public void clearProgressMap() {
        progressMap.clear();
    }

    public void updateProgress(String key, int value) {
        progressMap.put(key, value);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(progressMap, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
                e.printStackTrace();
            }
        }
    }

    public int getProgressValueByKey(String key) {
        return progressMap.get(key);
    }

    public void addKey(String key) {
        progressMap.put(key, 0);
    }
}
