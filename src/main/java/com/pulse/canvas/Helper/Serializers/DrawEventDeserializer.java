
package com.pulse.canvas.Helper.Serializers;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pulse.canvas.Dtoes.DrawEvent;
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class DrawEventDeserializer implements Deserializer<DrawEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public DrawEvent deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                return null;
            }
            return objectMapper.readValue(data, DrawEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing DrawEvent", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}