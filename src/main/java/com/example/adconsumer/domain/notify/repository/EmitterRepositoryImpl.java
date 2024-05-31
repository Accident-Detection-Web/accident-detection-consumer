package com.example.adconsumer.domain.notify.repository;

import com.example.adconsumer.domain.notify.entity.SseEmitterEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Repository
public class EmitterRepositoryImpl implements EmitterRepository{

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;


    private final SseEmitterRepository sseEmitterRepository;

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public EmitterRepositoryImpl(SseEmitterRepository sseEmitterRepository) {
        this.sseEmitterRepository = sseEmitterRepository;
    }

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);

        // Save to database
        SseEmitterEntity entity = new SseEmitterEntity();
        entity.setId(emitterId);
        entity.setUsername(emitterId.split("_")[0]);
        entity.setCreatedAt(LocalDateTime.now());
        sseEmitterRepository.save(entity);

        return sseEmitter;
    }

//    @Override
//    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
//        emitters.put(emitterId, sseEmitter);
//        return sseEmitter;
//    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
        List<SseEmitterEntity> entities = sseEmitterRepository.findByUsername(userId);

        // 로그 추가
        log.info("Found {} entities for userId {}", entities.size(), userId);
        entities.forEach(entity -> log.info("Entity ID: {}, Username: {}", entity.getId(), entity.getUsername()));

        return entities.stream()
            .collect(Collectors.toMap(SseEmitterEntity::getId, entity -> new SseEmitter(DEFAULT_TIMEOUT)));
    }

//    @Override
//    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
//        List<SseEmitterEntity> entities = sseEmitterRepository.findByUsername(userId);
//
//        // 로그 추가
//        log.info("Found {} entities for userId {}", entities.size(), userId);
//        entities.forEach(entity -> log.info("Entity ID: {}, Username: {}", entity.getId(), entity.getUsername()));
//
//        entities.forEach(entity -> {
//            SseEmitter emitter = emitters.get(entity.getId());
//            log.info("Checking emitter for ID: {} - Found: {}", entity.getId(), emitter != null);
//        });
//
//        return entities.stream()
//            .filter(entity -> emitters.containsKey(entity.getId()))
//            .collect(Collectors.toMap(SseEmitterEntity::getId, entity -> emitters.get(entity.getId())));
//    }

//    @Override
//    public Map<String, SseEmitter> findAllEmitterStartWithByUserId(String userId) {
//        return emitters.entrySet().stream()
//            .filter(entry -> entry.getKey().startsWith(userId))
//            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByUserId(String userId) {
        return eventCache.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(userId))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    @Override
    public void deleteById(String id) {
        emitters.remove(id);
        sseEmitterRepository.deleteById(id);
    }


//    @Override
//    public void deleteById(String id) {
//        emitters.remove(id);
//    }

    @Override
    public void deleteAllEmitterStartWithId(String userId) {
        List<SseEmitterEntity> entities = sseEmitterRepository.findByUsername(userId);
        for (SseEmitterEntity entity : entities) {
            emitters.remove(entity.getId());
            sseEmitterRepository.deleteById(entity.getId());
        }
    }

//    @Override
//    public void deleteAllEmitterStartWithId(String userId) {
//        emitters.forEach(
//            (key, emitter) ->{
//                if (key.startsWith(userId)) {
//                    emitters.remove(key);
//                }
//            }
//        );
//    }

    @Override
    public void deleteAllEventCacheStartWithId(String userId) {
        eventCache.forEach(
            (key, emitter) ->{
                if (key.startsWith(userId)) {
                    eventCache.remove(key);
                }
            }
        );
    }
}
