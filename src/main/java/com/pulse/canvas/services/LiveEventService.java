package com.pulse.canvas.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.ConnectedUserDTO;
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
    private final ConcurrentHashMap<Long, List<UserLiveEventDTO>> userEvents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long,List<ConnectedUserDTO>> connectedUsers = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;
    private static final int MAX_USER_JOIN_EVENTS = 50;
    public LiveEventService( ApplicationEventPublisher eventPublisher,ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user-events", groupId = "live-events-consumer",containerFactory = "userEventKafkaListenerContainerFactory")
    public void consume(UserLiveEventDTO userJoinedEventDTO) {
        eventPublisher.publishEvent(userJoinedEventDTO);


        userEvents.computeIfAbsent(userJoinedEventDTO.getCanvasId(), k -> new LinkedList<>());
        List<UserLiveEventDTO> list = userEvents.get(userJoinedEventDTO.getCanvasId());
        list.add(userJoinedEventDTO);

        switch (userJoinedEventDTO.getUserEventType()) {
            case USER_JOINED -> {
                ConnectedUserDTO newUser = new ConnectedUserDTO();
                newUser.setUserId(userJoinedEventDTO.getUserId());
                newUser.setConnectedAt(System.currentTimeMillis());
                newUser.setCanvasId(userJoinedEventDTO.getCanvasId());
                eventPublisher.publishEvent(newUser);
                connectedUsers.computeIfAbsent(userJoinedEventDTO.getCanvasId(), k -> new LinkedList<>());
                List<ConnectedUserDTO> usersList = connectedUsers.get(userJoinedEventDTO.getCanvasId());
                if (usersList != null && usersList.stream().noneMatch(user -> user.getUserId().equals(newUser.getUserId()))) {
                    usersList.add(newUser);
                }

            }
            case USER_LEFT -> {
                List<ConnectedUserDTO> usersList = connectedUsers.get(userJoinedEventDTO.getCanvasId());
                if (usersList != null) {
                    usersList.removeIf(user -> user.getUserId().equals(userJoinedEventDTO.getUserId()));
                }
            }
        }

        if (list.size() > MAX_USER_JOIN_EVENTS) {
            list.remove(0);
        }
    }

    public List<UserLiveEventDTO> getRecentEvents(Long canvasId) {
        return userEvents.getOrDefault(canvasId, Collections.emptyList());
    }

    private UserLiveEventDTO parseMessage(String message) {
        try {
            return objectMapper.readValue(message, UserLiveEventDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse event: " + message, e);
        }
    }
}
