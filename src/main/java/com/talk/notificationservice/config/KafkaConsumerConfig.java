package com.talk.notificationservice.config;

import com.talk.notificationservice.events.FriendRequestAcceptedEvent;
import com.talk.notificationservice.events.GroupMessageRepliedEvent;
import com.talk.notificationservice.events.NewMessageReactionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    @Value("${kafka.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Object> appEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        addTypeMapping(props);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> appEventKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(appEventConsumerFactory());

        return factory;
    }

    private void addTypeMapping(Map<String, Object> props) {
        Class[] subscribedEventClasses = {
                FriendRequestAcceptedEvent.class,
                GroupMessageRepliedEvent.class,
                NewMessageReactionEvent.class,
        };

        String typeMapping = "";
        for (Class eventClass : subscribedEventClasses) {
            String simpleName = eventClass.getSimpleName();
            String name = eventClass.getName();
            typeMapping += simpleName + ":" + name + ",";
        }

        // remove the last comma
        typeMapping = typeMapping.substring(0, typeMapping.length() - 1);

        // after the loop the typeMapping will be like this:
        // NewConversationEvent:com.imatalk.wshandlerservice.events.NewConversationEvent, NewFriendRequestEvent:com.imatalk.wshandlerservice.events.NewFriendRequestEvent,...

        props.put(JsonDeserializer.TYPE_MAPPINGS, typeMapping);

    }
}
