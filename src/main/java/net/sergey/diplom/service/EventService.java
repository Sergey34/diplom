package net.sergey.diplom.service;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {
    private final List<SseEmitter> emitters = new ArrayList<>();
    private static Map<String, Double> progressMap = new ConcurrentHashMap<>();
    ;


    public List<SseEmitter> getEmitters() {
        return emitters;
    }

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(SseEmitter emitter) {
        emitters.remove(emitter);
    }


    public void updateProgress(String key, double value) {
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

    public double getProgressValueByKey(String key) {
        return progressMap.get(key);
    }

    public void addKey(String key) {
        progressMap.put(key, 0.0);
    }
}
