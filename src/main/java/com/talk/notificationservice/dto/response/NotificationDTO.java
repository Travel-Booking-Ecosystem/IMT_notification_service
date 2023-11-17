package com.talk.notificationservice.dto.response;

import com.talk.notificationservice.entity.Notification;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class NotificationDTO {
    private String id;
    private String userId; // the user who receives the notification
    private String image;
    private String title;
    private String content;
    private boolean unread;
    private LocalDateTime createdAt;

    public NotificationDTO (Notification notification) {
        this.id = notification.getId();
        this.userId = notification.getUserId();
        this.image = notification.getImage();
        this.title = notification.getTitle();
        this.content = notification.getContent();
        this.unread = notification.isUnread();
        this.createdAt = notification.getCreatedAt();
    }

    public static List<NotificationDTO> from(List<Notification> notifications) {
        return notifications.stream().map(NotificationDTO::new).collect(Collectors.toList());
    }
}
