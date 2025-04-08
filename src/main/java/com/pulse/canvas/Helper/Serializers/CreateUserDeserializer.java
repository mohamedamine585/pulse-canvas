package com.pulse.canvas.Helper.Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pulse.canvas.Dtoes.CreateUserEvent;
import com.pulse.canvas.Dtoes.DrawEvent;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class CreateUserDeserializer implements Deserializer<CreateUserEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public CreateUserEvent deserialize(String topic, byte[] data) {
        try {

            if (data == null) {
                return null;
            }
            return objectMapper.readValue(data, CreateUserEvent.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing Create User Event", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
