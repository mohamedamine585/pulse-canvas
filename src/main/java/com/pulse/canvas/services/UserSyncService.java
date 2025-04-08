package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.CreateUserEvent;
import com.pulse.canvas.Helper.AppEventTriggers.CreateUserEventToSync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class UserSyncService {



    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    public UserSyncService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }



    @KafkaListener(topics = "auth.create-user",groupId = "user-sync-group",containerFactory = "createUserEventKafkaListenerContainerFactory")
    public void receiveCanvasSync(CreateUserEvent createUserEvent) {
        System.out.println("User Event Processing");


        CreateUserEventToSync createUserEventToSync = new CreateUserEventToSync(this);
        createUserEventToSync.setCreateUserEvent(createUserEvent);

        this.eventPublisher.publishEvent(createUserEventToSync);
    }
}
