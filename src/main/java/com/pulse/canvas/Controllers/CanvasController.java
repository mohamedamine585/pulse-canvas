package com.pulse.canvas.Controllers;

import com.pulse.canvas.Dtoes.CanvasDTO;
import com.pulse.canvas.entities.Canvas;
import com.pulse.canvas.Repositories.CanvasRepository;
import com.pulse.canvas.services.CanvasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canvas")
public class CanvasController {

    @Autowired
    private CanvasService canvasService;

    @PostMapping
    public CanvasDTO createCanvas(@RequestBody CanvasDTO canvas) {


        return canvasService.createCanvas(canvas);
    }

    @GetMapping("/{canvasId}")
    public Canvas getCanvas(@PathVariable Long canvasId) {
        return canvasService.getCanvas(canvasId);
    }

    @GetMapping("/creator/{creatorId}")
    public List<Canvas> getCanvasesByCreator(@PathVariable Long creatorId) {
        return canvasService.getCanvasesByCreator(creatorId);
    }

    @DeleteMapping("/{canvasId}")
    public void deleteCanvas(@PathVariable Long canvasId) {
        canvasService.deleteCanvas(canvasId);
    }
}