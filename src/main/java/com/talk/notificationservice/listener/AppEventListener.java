package com.talk.notificationservice.listener;

import com.talk.notificationservice.events.FriendRequestAcceptedEvent;
import com.talk.notificationservice.events.GroupMessageRepliedEvent;
import com.talk.notificationservice.events.NewMessageReactionEvent;
import com.talk.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@EnableKafka
@Component
@Slf4j
@RequiredArgsConstructor
public class AppEventListener {

    private static final String FRIEND_REQUEST_ACCEPTED_TOPIC = "friend-request-accepted";
    private static final String GROUP_MESSAGE_REPLIED_TOPIC = "group-message-replied";

    private final NotificationService notificationService;


    @KafkaListener(topics = FRIEND_REQUEST_ACCEPTED_TOPIC, containerFactory = "appEventKafkaListenerContainerFactory")
    public void handleFriendRequestAcceptedEvent(ConsumerRecord<String, FriendRequestAcceptedEvent> record) {
        log.info("Handling new friend request accepted event");
        FriendRequestAcceptedEvent event = record.value();
        notificationService.createFriendRequestAcceptedNotification(event);
    }

    @KafkaListener(topics = GROUP_MESSAGE_REPLIED_TOPIC, containerFactory = "appEventKafkaListenerContainerFactory")
    public void handleGroupMessageRepliedEvent(ConsumerRecord<String, GroupMessageRepliedEvent> record) {
        log.info("Handling new group message replied event");
        GroupMessageRepliedEvent event = record.value();
        notificationService.createFriendGroupMessageRepliedNotification(event);
    }

    @KafkaListener(topics = "new-message-reaction", containerFactory = "appEventKafkaListenerContainerFactory")
    public void handleNewMessageReactionEvent(ConsumerRecord<String, NewMessageReactionEvent> record) {
        log.info("Handling new message reaction event");
        NewMessageReactionEvent event = record.value();
        notificationService.createNewMessageReactionNotification(event);
    }
}
