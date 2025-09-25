package com.pulse.canvas.Helper.Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulse.canvas.Dtoes.UserLiveEventDTO;
import org.apache.kafka.common.serialization.Serializer;

public class UserEventDTOSerializer implements Serializer<UserLiveEventDTO> {
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String topic, UserLiveEventDTO data) {
        try {
            if (data == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing UserJoinedEventDTO", e);
        }
    }
}
