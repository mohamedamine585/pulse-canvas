package com.pulse.canvas.Controllers;

import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.Repositories.CanvasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/canvas")
public class CanvasController {

    @Autowired
    private CanvasRepository canvasRepository;

    @PostMapping
    public Canvas createCanvas(@RequestBody Canvas canvas) {
        return canvasRepository.save(canvas);
    }

    @GetMapping("/creator/{creatorId}")
    public List<Canvas> getCanvasesByCreator(@PathVariable Long creatorId) {
        return canvasRepository.findByCreatorId(creatorId);
    }

    @DeleteMapping("/{canvasId}")
    public void deleteCanvas(@PathVariable Long canvasId) {
        canvasRepository.deleteById(canvasId);
    }
}