package com.pulse.canvas.services;

import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.JoinCanvas;
import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import com.pulse.canvas.Repositories.JoinCanvasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JoinCanvasService {

    @Autowired
    private JoinCanvasRepository joinCanvasRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private CanvasRepository canvasRepository;

    public JoinCanvas joinArtistToCanvas(Long artistId, Long canvasId) {
        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> new RuntimeException("Artist not found"));
        Canvas canvas = canvasRepository.findById(canvasId).orElseThrow(() -> new RuntimeException("Canvas not found"));

        JoinCanvas joinCanvas = new JoinCanvas();
        joinCanvas.setArtist(artist);
        joinCanvas.setCanvas(canvas);
        return joinCanvasRepository.save(joinCanvas);
    }
}