package com.pulse.canvas.Controllers;

import com.pulse.canvas.entities.JoinCanvas;
import com.pulse.canvas.services.JoinCanvasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/join")
public class JoinCanvasController {

    @Autowired
    private JoinCanvasService joinCanvasService;

    @PostMapping
    public JoinCanvas joinArtistToCanvas(@RequestParam Long artistId, @RequestParam Long canvasId) {
        return joinCanvasService.joinArtistToCanvas(artistId, canvasId);
    }
}