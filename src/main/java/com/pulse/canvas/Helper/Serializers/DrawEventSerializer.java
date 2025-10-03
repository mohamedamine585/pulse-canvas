package com.pulse.canvas.Helper.Serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pulse.canvas.Dtoes.DrawEvent;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DrawEventSerializer implements Serializer<DrawEvent> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, DrawEvent drawEvent) {
        // Convert the CanvasMessage object to a JSON string
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(drawEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // Return the byte array of the JSON string
        return jsonString.getBytes();
    }
}

