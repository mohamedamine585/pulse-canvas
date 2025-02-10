package com.pulse.canvas.Controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class CanvasLiveController {

    // This method is triggered when a message is sent to "/app/draw"
    @MessageMapping("/draw")
    @SendTo("/topic/canvas")
    public String handleDrawMessage(String message) {
        // Here you can process the incoming message (e.g., drawing instructions)
        System.out.println("Received drawing message: " + message);
        // Returning the message to be sent to all connected clients
        return message;  // You can also return a more complex object if needed
    }
}
