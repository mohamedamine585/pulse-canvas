package com.pulse.canvas.configurations;


import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Dtoes.UserLiveEventDTO;
import com.pulse.canvas.Helper.Serializers.DrawEventSerializer;
import com.pulse.canvas.Helper.Serializers.UserEventDTOSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, DrawEvent> drawEventProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, DrawEventSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, DrawEvent> drawEventKafkaTemplate() {
        return new KafkaTemplate<>(drawEventProducerFactory());
    }

    @Bean
    public ProducerFactory<String, UserLiveEventDTO> userJoinedProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, UserEventDTOSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    @Bean
    public KafkaTemplate<String, UserLiveEventDTO> userJoinedKafkaTemplate() {
        return new KafkaTemplate<>(userJoinedProducerFactory());
    }


}