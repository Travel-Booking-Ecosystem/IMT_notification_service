package com.talk.notificationservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewNotificationEvent {
    private String userId; // the user who receives the notification
    private Notification notification;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Notification {
        private String id;
        private String userId; // the user who receives the notification
        private String image;
        private String title;
        private String content;
        private boolean unread;
        private LocalDateTime createdAt;

    }
}
