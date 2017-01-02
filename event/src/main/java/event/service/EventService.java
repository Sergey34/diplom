package event.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class EventService {
    private final Map<String, String> progressMap = new ConcurrentHashMap<>();
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

    public synchronized void updateProgress(String key, Double value) {
        String valueStr = String.format(Locale.ENGLISH, "%.2f", value);
        progressMap.put(key, valueStr);
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(progressMap, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
                log.warn("Ошибка отправки увеомления о работе парсера {}", e);
            }
        }
    }

    public double getProgressValueByKey(String key) {
        return Double.parseDouble(progressMap.get(key));
    }

    public void addKey(String key) {
        progressMap.put(key, "0.0");
    }
}
