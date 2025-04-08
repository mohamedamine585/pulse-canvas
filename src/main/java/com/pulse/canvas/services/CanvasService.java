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

    public CanvasDTO createCanvas(CanvasDTO canvas) {
        try {
            System.out.println("Creating canvas");
            final Authentication authentication =  SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null)
                throw new Exception("Authentication is null");
            System.out.println(authentication.getPrincipal());
            final Claims claims = (Claims) authentication.getPrincipal();
            final Long userId = Long.valueOf(claims.getSubject());
            Artist artist = getOrCreateArtist(userId);
            Canvas newCanvas = new Canvas();
            newCanvas.setName(canvas.getCanvasName());
            newCanvas.setCreator(artist);

             newCanvas = canvasRepository.save(newCanvas);
            CanvasDTO canvasDTO = new CanvasDTO();

            canvasDTO.setId(newCanvas.getId());
            canvasDTO.setCanvasName(newCanvas.getName());
            canvasDTO.setCreatorId(newCanvas.getCreator().getId());
            return canvasDTO;
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
    public Artist getOrCreateArtist(Long userId) {
        Optional<Artist> artistOptional = artistRepository.findById(userId);
        return artistOptional.orElse(null);
    }

    public Canvas getOrCreateCanvas(Long canvasId, Artist artist) {
        try {
            Optional<Canvas> optionalCanvas = canvasRepository.findById(canvasId);
            if (optionalCanvas.isPresent()) {
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