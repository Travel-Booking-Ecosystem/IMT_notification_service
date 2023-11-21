package com.talk.notificationservice.service;


import com.talk.notificationservice.dto.response.CommonResponse;
import com.talk.notificationservice.dto.response.NotificationDTO;
import com.talk.notificationservice.entity.Notification;
import com.talk.notificationservice.events.FriendRequestAcceptedEvent;
import com.talk.notificationservice.events.GroupMessageRepliedEvent;
import com.talk.notificationservice.events.NewMessageReactionEvent;
import com.talk.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final KafkaProducerService kafkaProducerService;


    public ResponseEntity<CommonResponse> getNotifications(String currentUserId) {
        List<Notification> allByUserIdOrderByCreatedAtDesc = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId);
        List<NotificationDTO> notificationDTOList = NotificationDTO.from(allByUserIdOrderByCreatedAtDesc);
        CommonResponse commonResponse = CommonResponse.success("Get notifications successfully", notificationDTOList);
        return ResponseEntity.ok(commonResponse);
    }


    public ResponseEntity<CommonResponse> seeAllNotifications(String currentUserId) {
        List<Notification> allUnreadNotifications = notificationRepository.findAllByUserIdAndUnread(currentUserId, true);

        for (Notification notification : allUnreadNotifications) {
            notification.setUnread(false);
        }
        notificationRepository.saveAll(allUnreadNotifications);

        return ResponseEntity.ok(CommonResponse.success("See all notifications successfully"));

    }

    public void createFriendRequestAcceptedNotification(FriendRequestAcceptedEvent event) {
        // create notification for friend request sender to notify that his/her friend request is accepted
        Notification notification = Notification.builder()
                .userId(event.getSenderId())
                .image(event.getReceiver().getAvatar())
                .title(event.getReceiver().getDisplayName())
                .content(event.getReceiver().getDisplayName() + " accepted your friend request")
                .unread(true)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);


        // produce NEW_NOTIFICATION event to Kafka
         kafkaProducerService.sendNewNotificationEvent(notification);
    }

    public void createFriendGroupMessageRepliedNotification(GroupMessageRepliedEvent event) {
        // send notification to the sender of the message whose being replied to
        Notification notification = Notification.builder()
                .userId(event.getMessage().getSenderId())
                .image(event.getConversation().getAvatar())
                .title(event.getConversation().getName())
                .content(event.getReplier().getDisplayName() + " replied to your message")
                .unread(true)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // produce NEW_NOTIFICATION event to Kafka
        kafkaProducerService.sendNewNotificationEvent(notification);
    }

    public void createNewMessageReactionNotification(NewMessageReactionEvent event) {
        // if the reaction is unreact, don't create notification
        if (event.getMessageReaction().isUnReact()) return;

        // send notification to the sender of the message whose being reacted to
        Notification notification = Notification.builder()
                        .userId(event.getMessageReaction().getMessageOwnerId())
                                .image(event.getMessageReaction().getConversation().getConversationAvatar())
                                        .title(event.getMessageReaction().getConversation().getConversationName())
                                                .content(event.getMessageReaction().getReactionInformation().getReactorName() + " reacted to your message")
                                                        .unread(true)
                                                                .createdAt(LocalDateTime.now())
                                                                        .build();


        notificationRepository.save(notification);

        log.info("Notification: {}", notification);
        // produce NEW_NOTIFICATION event to Kafka
        kafkaProducerService.sendNewNotificationEvent(notification);
    }
}
