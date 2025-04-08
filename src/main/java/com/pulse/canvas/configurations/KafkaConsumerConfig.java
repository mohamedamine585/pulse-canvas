package com.pulse.canvas.configurations;

import com.pulse.canvas.Dtoes.CreateUserEvent;
import com.pulse.canvas.Dtoes.DrawEvent;
import com.pulse.canvas.Helper.Serializers.CreateUserDeserializer;
import com.pulse.canvas.Helper.Serializers.DrawEventDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Autowired
    private String appInstanceId;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    // ConsumerFactory for DrawEvent
    @Bean
    public ConsumerFactory<String, DrawEvent> drawEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "canvas-sync-group-" + 3);  // Ensure the group id is set here
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, DrawEventDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    // ConsumerFactory for CreateUserEvent
    @Bean
    public ConsumerFactory<String, CreateUserEvent> createUserEventConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "canvas-sync-group-" + appInstanceId + "-create-user");  // Ensure the group id is set here
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CreateUserDeserializer.class); // Correct deserializer for CreateUserEvent
        return new DefaultKafkaConsumerFactory<>(configProps);
    }

    // KafkaListenerContainerFactory for DrawEvent
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DrawEvent> drawEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DrawEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(drawEventConsumerFactory());
        return factory;
    }

    // KafkaListenerContainerFactory for CreateUserEvent
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CreateUserEvent> createUserEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CreateUserEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createUserEventConsumerFactory());
        return factory;
    }
}
