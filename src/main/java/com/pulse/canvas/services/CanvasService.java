package com.pulse.canvas.services;

import com.pulse.canvas.Dtoes.CanvasDTO;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
public class CanvasService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private CanvasRepository canvasRepository;

    @Autowired
    private CanvasPrintRepository canvasPrintRepository;

    public Canvas createCanvas(CanvasDTO canvas) {
        try {
            final Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null)
                throw new Exception("Authentication is null");
            System.out.println(authentication.getPrincipal());
            final Claims claims = (Claims) authentication.getPrincipal();
            final String email = claims.getSubject();
            System.out.println(email);
            Artist artist = getOrCreateArtist(email);
            Canvas newCanvas = new Canvas();
            newCanvas.setName(canvas.getCanvasName());
            newCanvas.setCreator(artist);
            return canvasRepository.save(newCanvas);
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        return null;}
    public List<Canvas> getCanvasesByCreator(Long creatorId) {
        return canvasRepository.findByCreatorId(creatorId);
    }

    public void deleteCanvas(Long canvasId) {
        canvasRepository.deleteById(canvasId);
    }
    public Canvas getCanvas(Long canvasId) {
        return canvasRepository.findById(canvasId).orElse(null);
    }
    public Artist getOrCreateArtist(String username) {
        Artist artist = artistRepository.findByUsername(username);
        if (artist == null) {
            artist = new Artist();
            artist.setUsername("artist" + new Random().nextInt(1000));
            artistRepository.save(artist);
        }
        return artist;
    }

    public Canvas getOrCreateCanvas(Long canvasId, Artist artist) {
        try {
            Optional<Canvas> optionalCanvas = canvasRepository.findById(canvasId);
            if (optionalCanvas.isPresent()) {
                if(!Objects.equals(optionalCanvas.get().getCreator().getId(), artist.getId()))
                    throw new Exception("Canvas does not belong to the artist");
                return optionalCanvas.get();
            } else {
                Canvas newCanvas = new Canvas();
                newCanvas.setName("Canvas " + new Random().nextInt(1000));
                newCanvas.setCreator(artist);
                return canvasRepository.save(newCanvas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public CanvasPrint getOrCreateCanvasPrint(Canvas canvas) {
        CanvasPrint canvasPrint = canvasPrintRepository.findByCanvasId(canvas.getId());
        if (canvasPrint == null) {
            canvasPrint = new CanvasPrint();
            canvasPrint.setCanvas(canvas);
            canvasPrint.setPrint(new byte[0]);
            return canvasPrintRepository.save(canvasPrint);
        }
        return canvasPrint;
    }
}