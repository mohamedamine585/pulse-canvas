package com.pulse.canvas.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.UserLiveEventDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LiveEventService {

    private  ObjectMapper objectMapper = new ObjectMapper();
    @Qualifier("userJoinedKafkaTemplate")
    private final Map<Long, List<UserLiveEventDTO>> userJoinsEvents = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;
    private static final int MAX_USER_JOIN_EVENTS = 50;
    public LiveEventService( ApplicationEventPublisher eventPublisher,ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-events", groupId = "live-events-consumer",containerFactory = "userEventKafkaListenerContainerFactory")
    public void consume(UserLiveEventDTO userJoinedEventDTO) {
        System.out.println("Received UserJoinedEventDTO: " + userJoinedEventDTO);
        eventPublisher.publishEvent(userJoinedEventDTO);
        userJoinsEvents.computeIfAbsent(userJoinedEventDTO.getCanvasId(), k -> new LinkedList<>());
        List<UserLiveEventDTO> list = userJoinsEvents.get(userJoinedEventDTO.getCanvasId());
        list.add(userJoinedEventDTO);

        if (list.size() > MAX_USER_JOIN_EVENTS) {
            list.remove(0);
        }
    }

    public List<UserLiveEventDTO> getRecentEvents(Long canvasId) {
        return userJoinsEvents.getOrDefault(canvasId, Collections.emptyList());
    }

    private UserLiveEventDTO parseMessage(String message) {
        try {
            return objectMapper.readValue(message, UserLiveEventDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse event: " + message, e);
        }
    }
}
