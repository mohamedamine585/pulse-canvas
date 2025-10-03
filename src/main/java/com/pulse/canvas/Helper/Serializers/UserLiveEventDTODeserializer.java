package com.pulse.canvas.Helper.Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.UserLiveEventDTO;
import org.apache.kafka.common.serialization.Deserializer;

public class UserLiveEventDTODeserializer implements Deserializer<UserLiveEventDTO> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public UserLiveEventDTO deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                return null;
            }
            return objectMapper.readValue(data, UserLiveEventDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing UserJoinedEventDTO", e);
        }
    }
}
