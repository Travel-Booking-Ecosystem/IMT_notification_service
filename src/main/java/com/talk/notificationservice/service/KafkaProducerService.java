package com.talk.notificationservice.service;

import com.talk.notificationservice.entity.Notification;
import com.talk.notificationservice.events.NewNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.new-notification}")
    private String NEW_NOTIFICATION_TOPIC;

    public void sendNewNotificationEvent(Notification notification) {

        NewNotificationEvent.Notification build = NewNotificationEvent.Notification.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .image(notification.getImage())
                .title(notification.getTitle())
                .content(notification.getContent())
                .unread(notification.isUnread())
                .createdAt(notification.getCreatedAt())
                .build();

        NewNotificationEvent newNotificationEvent = NewNotificationEvent.builder()
                .userId(notification.getUserId())
                .notification(build)
                .build();

        kafkaTemplate.send(NEW_NOTIFICATION_TOPIC, newNotificationEvent);
    }
}
