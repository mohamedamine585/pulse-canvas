package com.pulse.canvas.Controllers;

import com.pulse.canvas.Dtoes.CreateUserEvent;
import com.pulse.canvas.Helper.AppEventTriggers.CreateUserEventToSync;
import com.pulse.canvas.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {

    @Autowired
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    private ArtistService artistService;

    public UserConsumer(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @KafkaListener(topics = "auth.user", groupId = "auth-user-group",containerFactory = "createUserEventKafkaListenerContainerFactory")
    public void consume(CreateUserEvent message) {
        try {

            CreateUserEventToSync createUserEventToSync = new CreateUserEventToSync(this);
            createUserEventToSync.setCreateUserEvent(message);
            artistService.createArtistFromEvent(createUserEventToSync);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}