package net.sergey.diplom.services.mainservice;


import net.sergey.diplom.services.utils.UtilsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class EventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UtilsLogger.getStaticClassName());
    private final Map<String, String> progressMap = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<SseEmitter> emitters = new ConcurrentLinkedQueue<>();

    public ConcurrentLinkedQueue<SseEmitter> getEmitters() {
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
            } catch (Exception e) {
                emitter.complete();
                emitters.remove(emitter);
                LOGGER.warn("Ошибка отправки увеомления о работе парсера", e);
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
