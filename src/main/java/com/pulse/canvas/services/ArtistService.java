package com.pulse.canvas.services;


import com.pulse.canvas.Dtoes.CreateUserEvent;
import com.pulse.canvas.Helper.AppEventTriggers.CreateUserEventToSync;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.entities.Artist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service

public class ArtistService {

    private static final Logger log = LoggerFactory.getLogger(ArtistService.class);
    @Autowired
    ArtistRepository artistRepository;
    @EventListener
    public void createArtistFromEvent(CreateUserEventToSync createUserEventToSync){
        CreateUserEvent createUserEvent = createUserEventToSync.getCreateUserEvent();
        System.out.println(createUserEvent);
        try {

            if(artistRepository.findById(createUserEvent.getId()).isPresent() ){

                log.error("Artist with id {} already exists",createUserEvent.getId());
                return;
            }

            Artist artist = new Artist();
            artist.setUsername(createUserEvent.getUsername());
            artist.setEmail(createUserEvent.getEmail());
            artist.setId(createUserEvent.getId());



            artistRepository.save(artist);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
