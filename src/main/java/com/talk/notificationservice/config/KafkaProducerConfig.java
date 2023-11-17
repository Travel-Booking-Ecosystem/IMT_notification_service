package com.talk.notificationservice.config;
//TODO: KAFKA PRODUCER CONFIG

import com.talk.notificationservice.events.GroupMessageRepliedEvent;
import com.talk.notificationservice.events.NewNotificationEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${topic.new-conversation}")
    private String NEW_NOTIFICATION_TOPIC;




    @Bean
    public NewTopic newNotificationTopic() {
        return TopicBuilder.name(NEW_NOTIFICATION_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }




    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        addTypeMapping(props);

        ProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);

        return new KafkaTemplate<>(factory);
    }

    private void addTypeMapping(Map<String, Object> props) {
        Class[] producedEventClasses = {
                NewNotificationEvent.class,
                NewNotificationEvent.class
        };

        String typeMapping = "";
        for (Class eventClass : producedEventClasses) {
            String simpleName = eventClass.getSimpleName();
            String name = eventClass.getName();
            typeMapping += simpleName + ":" + name + ",";
        }
        // remove the last comma
        typeMapping = typeMapping.substring(0, typeMapping.length() - 1);

        // after the loop the typeMapping will be like this:
        // NewConversationEvent:com.imatalk.chat-service.events.NewConversationEvent, NewFriendRequestEvent:com.imatalk.chat-service.events.NewFriendRequestEvent,...

        props.put(JsonSerializer.TYPE_MAPPINGS, typeMapping);

    }
}
