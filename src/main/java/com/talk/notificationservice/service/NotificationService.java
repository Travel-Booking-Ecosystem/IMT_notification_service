package com.talk.notificationservice.service;


import com.talk.notificationservice.dto.response.CommonResponse;
import com.talk.notificationservice.dto.response.NotificationDTO;
import com.talk.notificationservice.entity.Notification;
import com.talk.notificationservice.events.FriendRequestAcceptedEvent;
import com.talk.notificationservice.events.GroupMessageRepliedEvent;
import com.talk.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        Notification notification = Notification.builder()
                .userId(event.getReceiverId())
                .image(event.getSender().getAvatar())
                .title(event.getSender().getDisplayName())
                .content(event.getSender().getDisplayName() + " accepted your friend request")
                .unread(true)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);


        // produce NEW_NOTIFICATION event to Kafka
         kafkaProducerService.sendNewNotificationEvent(notification);
    }

    public void createFriendGroupMessageRepliedNotification(GroupMessageRepliedEvent event) {
        Notification notification = Notification.builder()
                .userId(event.getSender().getId())
                .image(event.getSender().getAvatar())
                .title(event.getSender().getDisplayName())
                .content(event.getSender().getDisplayName() + " replied to your message")
                .unread(true)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // produce NEW_NOTIFICATION event to Kafka
        kafkaProducerService.sendNewNotificationEvent(notification);
    }
}
