package com.pulse.canvas.services;

import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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