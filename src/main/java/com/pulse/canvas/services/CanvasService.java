package com.pulse.canvas.services;

import com.pulse.canvas.Repositories.ArtistRepository;
import com.pulse.canvas.Repositories.CanvasPrintRepository;
import com.pulse.canvas.Repositories.CanvasRepository;
import com.pulse.canvas.Repositories.JoinCanvasRepository;
import com.pulse.canvas.entities.Artist;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.entities.CanvasPrint;
import com.pulse.canvas.entities.JoinCanvas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CanvasService {
    @Autowired
    private CanvasRepository canvasRepository;
    @Autowired
    private CanvasPrintRepository canvasPrintRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private JoinCanvasRepository joinCanvasRepository;

    @Transactional
    public Canvas createCanvas(String name , Integer size , Long artistId) {
        // Find the artist by ID
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));



        // Create and save the new canvas
        Canvas canvas = new Canvas();
        canvas.setName(name);
        canvas.setCreator(artist);
        canvas = canvasRepository.save(canvas);

        // create a print
        CanvasPrint canvasPrint = new CanvasPrint();
        canvasPrint.setCanvas(canvas);
        canvasPrint.setPrint(new byte[size]);
        canvasPrintRepository.save(canvasPrint);


        // Optionally, you can create a JoinCanvas entity to represent the artist joining the canvas
        JoinCanvas joinCanvas = new JoinCanvas();
        joinCanvas.setCanvas(canvas);
        joinCanvas.setArtist(artist);
        joinCanvasRepository.save(joinCanvas);

        return canvas;
    }

    @Transactional
    public JoinCanvas joinCanvas(Long canvasId, Long artistId) {
        // Find the canvas and artist by their IDs
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new IllegalArgumentException("Canvas not found"));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));

        // Create and save a JoinCanvas entry
        JoinCanvas joinCanvas = new JoinCanvas();
        joinCanvas.setCanvas(canvas);
        joinCanvas.setArtist(artist);
        return joinCanvasRepository.save(joinCanvas);
    }

    @Transactional
    public void deleteCanvas(Long canvasId) {
        // Check if the canvas exists
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new IllegalArgumentException("Canvas not found"));

        // Delete the canvas (this will automatically delete related JoinCanvas entries, if cascade delete is configured)
        joinCanvasRepository.deleteByCanvas(canvas);
        canvasRepository.delete(canvas);
    }

    @Transactional
    public Canvas updateCanvas(Long canvasId, String newName) {
        // Find the canvas by ID
        Canvas canvas = canvasRepository.findById(canvasId)
                .orElseThrow(() -> new IllegalArgumentException("Canvas not found"));

        // Update the canvas name and save
        canvas.setName(newName);
        return canvasRepository.save(canvas);
    }
}
