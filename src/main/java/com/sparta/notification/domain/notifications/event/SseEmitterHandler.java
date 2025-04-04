package com.sparta.notification.domain.notifications.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseEmitterHandler {

    private final Map<String, Set<SseEmitter>> emittersPerTopic = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String topic) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emittersPerTopic.computeIfAbsent(topic, key -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(emitter);

        emitter.onCompletion(() -> removeEmitter(topic, emitter));
        emitter.onTimeout(() -> removeEmitter(topic, emitter));
        emitter.onError(e -> removeEmitter(topic, emitter));

        return emitter;
    }

    private void removeEmitter(String topic, SseEmitter emitter) {
        Set<SseEmitter> emitters = emittersPerTopic.get(topic);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emittersPerTopic.remove(topic);
            }
        }
    }

    public void broadcast(String topic, String data) {
        Set<SseEmitter> emitters = emittersPerTopic.get(topic);
        log.info("📢 Current subscribers for {}: {}", topic, (emitters != null ? emitters.size() : 0));
        if (emitters != null) {
            log.info("📡 Broadcasting to {} subscribers on topic: {}", emitters.size(), topic);
            emitters.removeIf(emitter -> !sendEvent(emitter, data));
        } else {
            log.warn("⚠ No subscribers found for topic: {}", topic);
        }
    }

    private boolean sendEvent(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().name("notification").data(data));
            log.info("✅ SSE event sent successfully: {}", data);
            return true;
        } catch (Exception e) {
            // 전송 실패 로그 기록
            System.err.println("Failed to send SSE: " + e.getMessage());
            return false;
        }
    }
}
